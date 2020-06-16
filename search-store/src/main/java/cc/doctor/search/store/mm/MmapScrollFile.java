package cc.doctor.search.store.mm;

import cc.doctor.search.common.entity.Action;
import cc.doctor.search.common.entity.Tuple;
import cc.doctor.search.common.exceptions.file.FileException;
import cc.doctor.search.common.exceptions.file.MmapFileException;
import cc.doctor.search.common.utils.CollectionUtils;
import cc.doctor.search.common.utils.FileUtils;
import cc.doctor.search.common.utils.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import static cc.doctor.search.store.mm.ScrollFile.AutoIncrementScrollFileNameStrategy.autoIncrementScrollFileNameStrategy;

/**
 * Created by doctor on 2017/3/16.
 * 两个文件指针,一个写,一个读
 */
public class MmapScrollFile implements ScrollFile {
    private static final Logger log = LoggerFactory.getLogger(ScrollFile.class);
    private String root;
    private int scrollSize;
    private MmapFile mmapFile;
    private MmapFile readFile;
    private List<String> files = new LinkedList<>();
    private long position = 0;
    private String current;
    private ScrollFileNameStrategy scrollFileNameStrategy;
    private Action onWriteFileCheckAction;

    public MmapScrollFile(String root, int scrollSize) {
        this(root, scrollSize, autoIncrementScrollFileNameStrategy);
    }

    public MmapScrollFile(String root, int scrollSize, ScrollFileNameStrategy scrollFileNameStrategy) {
        this(root, scrollSize, scrollFileNameStrategy, 0);
    }

    public MmapScrollFile(String root, int scrollSize, ScrollFileNameStrategy scrollFileNameStrategy, long position) {
        this.root = root;
        this.scrollFileNameStrategy = scrollFileNameStrategy;
        this.scrollSize = scrollSize;
        files = FileUtils.list(root, CollectionUtils.list("checkpoint"));
        String file = getFile(position);
        current = file;
        try {
            if (file == null) {
                current = scrollFileNameStrategy.first();
                String currentFileAbsolute = root + "/" + current;
                if (!FileUtils.exists(currentFileAbsolute)) {
                    FileUtils.createFileRecursion(currentFileAbsolute);
                }
                mmapFile = new MmapFile(currentFileAbsolute, scrollSize);
            } else {
                int filePosition = (int) (position % scrollSize);
                mmapFile = new MmapFile(root + "/" + current, scrollSize, filePosition);
            }
            readFile = mmapFile;
        } catch (IOException e) {
            log.error("", e);
        }
    }

    @Override
    public String root() {
        return root;
    }

    @Override
    public int scrollSize() {
        return scrollSize;
    }

    @Override
    public List<String> files() {
        return files;
    }

    @Override
    public long position() {
        return position;
    }

    @Override
    public String getFile(long position) {
        int index = (int) (position / scrollSize);
        if (index >= files.size()) {
            return null;
        }
        return files.get(index);
    }

    @Override
    public String current() {
        return current;
    }

    @Override
    public String next() {
        return scrollFileNameStrategy.next(current);
    }

    @Override
    public <T extends Serializable> long writeSerializable(T serializable) {
        long start = position;
        try {
            byte[] bytes = SerializeUtils.serialize(serializable);
            if ((position % scrollSize + 4 + bytes.length) > scrollSize) {
                String nextFile = next();
                mmapFile.commit();
                mmapFile.clean();
                String nextFileAbsolute = root + "/" + nextFile;
                if (!FileUtils.exists(nextFileAbsolute)) {
                    FileUtils.createFileRecursion(nextFileAbsolute);
                }
                if (onWriteFileCheckAction != null) {
                    onWriteFileCheckAction.doAction();
                }
                mmapFile = new MmapFile(nextFileAbsolute, scrollSize);
                current = nextFile;
                position = files.size() * scrollSize;
                files.add(current);
            }
            mmapFile.appendInt(bytes.length);
            mmapFile.appendBytes(bytes);
            start = position;
            position += bytes.length;
        } catch (IOException e) {
            log.error("", e);
        }
        return start;
    }

    @Override
    public <T extends Serializable> Tuple<Long, T> readSerializable(long position) {
        int positionInFile = (int) (position % scrollSize);
        int index = (int) (position / scrollSize);
        String file = getFile(position);
        if (file == null) {
            return null;
        }
        if (!file.equals(readFile.getFile().getName())) {
            try {
                readFile = new MmapFile(root + "/" + file, scrollSize, positionInFile);
            } catch (IOException e) {
                log.error("", e);
                throw new MmapFileException("Create mmap file error.");
            }
        }
        int size = readFile.readInt(positionInFile);
        if (size == 0) {
            return readSerializable((index + 1) * scrollSize);
        }
        long nextPos = index * scrollSize + positionInFile + 4 + size;
        return new Tuple<>(nextPos, readFile.<T>readObject(positionInFile + 4, size));
    }

    @Override
    public void writeLock() {

    }

    @Override
    public void readLock() {

    }

    @Override
    public void onWriteFileCheck(Action action) {
        onWriteFileCheckAction = action;
    }

    @Override
    public <T extends Serializable> long writeSerializable(long position, T serializable) {
        int positionInFile = (int) (position % scrollSize);
        String file = getFile(position);
        if (file == null) {
            throw new FileException("Position over limit.");
        }
        if (!file.equals(mmapFile.getFile().getName())) {
            try {
                mmapFile = new MmapFile(root + "/" + file, scrollSize, positionInFile);
            } catch (IOException e) {
                log.error("", e);
            }
        }

        return 0;
    }

    @Override
    public void position(long position) {
        mmapFile.setPosition((int) (position % scrollSize));
    }
}
