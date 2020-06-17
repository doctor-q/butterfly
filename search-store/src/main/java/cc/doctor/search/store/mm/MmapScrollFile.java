package cc.doctor.search.store.mm;

import cc.doctor.search.common.entity.Action;
import cc.doctor.search.common.utils.FileUtils;
import cc.doctor.search.common.utils.SerializeUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static cc.doctor.search.store.mm.AutoIncrementScrollFileNameStrategy.autoIncrementScrollFileNameStrategy;

/**
 * Created by doctor on 2017/3/16.
 * scroll file, mmap two file, one write, one read
 */
@Slf4j
public class MmapScrollFile implements ScrollFile, AppendFile {
    /**
     * base directory
     */
    private String root;

    /**
     * single file size
     */
    private int scrollSize;
    /**
     * write memory file
     */
    private MmapFile writeFile;
    /**
     * read memory file
     */
    private MmapFile readFile;
    /**
     * scroll files
     */
    private List<String> files = new LinkedList<>();
    /**
     * global write position
     */
    private long writePosition;
    /**
     * scroll name strategy
     */
    private ScrollFileNameStrategy scrollFileNameStrategy;
    /**
     * file change action
     */
    private Action onWriteFileCheckAction;

    public MmapScrollFile(String root, int scrollSize) {
        this(root, scrollSize, autoIncrementScrollFileNameStrategy);
    }

    public MmapScrollFile(String root, int scrollSize, ScrollFileNameStrategy scrollFileNameStrategy) {
        this.root = root;
        this.scrollFileNameStrategy = scrollFileNameStrategy;
        this.scrollSize = scrollSize;
        files = FileUtils.list(root, null);
        // get the last file and position
        String lastFile = scrollFileNameStrategy.last(files);
        try {
            if (lastFile == null) {
                lastFile = scrollFileNameStrategy.first();
                files.add(lastFile);
            }
            writeFile = new MmapFile(root + "/" + lastFile, scrollSize);
            writePosition = (long) (files.size() - 1) * scrollSize + writeFile.getWritePosition();
            readFile = writeFile;
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
    public long getWritePosition() {
        return writePosition;
    }

    @Override
    public void setWritePosition(long writePosition) {
        if (writePosition > this.writePosition) {
            return;
        }
        this.writePosition = writePosition;
        seekMmapFile(this.writePosition, false);
    }

    /**
     * create a new mmap file and switch write file to it
     */
    private void nextWriteFile() {
        String nextFile = scrollFileNameStrategy.next(writeFile.getName());
        writeFile.commit();
        writeFile.close();
        String nextFileAbsolute = root + "/" + nextFile;
        if (onWriteFileCheckAction != null) {
            onWriteFileCheckAction.doAction();
        }
        try {
            writeFile = new MmapFile(nextFileAbsolute, scrollSize, 0);
            writePosition = (long) files.size() * scrollSize;
            files.add(nextFile);
        } catch (IOException e) {
            log.error("", e);
        }
    }

    @Override
    public void onWriteFileCheck(Action action) {
        onWriteFileCheckAction = action;
    }

    @Override
    public void close() {
        writeFile.close();
        readFile.close();
    }

    @Override
    public boolean canAppend(int size) {
        return true;
    }

    @Override
    public boolean canRead(long position, int size) {
        return position + size < writePosition;
    }

    @Override
    public void appendBytes(byte[] bytes) {
        if (!writeFile.canAppend(bytes.length)) {
            nextWriteFile();
        }
        writeFile.appendBytes(bytes);
    }

    @Override
    public void appendInt(int aInt) {
        if (!writeFile.canAppend(4)) {
            nextWriteFile();
        }
        writeFile.appendInt(aInt);
    }

    @Override
    public void appendLong(long aLong) {
        if (!writeFile.canAppend(8)) {
            nextWriteFile();
        }
        writeFile.appendLong(aLong);
    }

    @Override
    public <T extends Serializable> int writeObject(T serializable) {
        try {
            byte[] bytes = SerializeUtils.serialize(serializable);
            if (!writeFile.canAppend(4 + bytes.length)) {
                nextWriteFile();
            }
            return writeFile.writeObject(serializable);
        } catch (IOException e) {
            log.error("", e);
        }
        return 0;
    }

    private void seekMmapFile(long position, boolean read) {
        String file = this.scrollFileNameStrategy.getFileByPosition(position, scrollSize);
        if (!files.contains(file)) {
            throw new IllegalArgumentException();
        }
        MmapFile mmapFile = read ? readFile : writeFile;
        int positionInFile = (int) (position % scrollSize);
        if (!file.equals(mmapFile.getFile().getName())) {
            mmapFile.close();
            try {
                if (read) {
                    this.readFile = new MmapFile(root + "/" + file, scrollSize);
                } else {
                    this.writeFile = new MmapFile(root + "/" + file, scrollSize, positionInFile);
                }
            } catch (IOException e) {
                log.error("", e);
            }
        } else if (!read) {
            mmapFile.setWritePosition(positionInFile);
        }
    }

    private void seekReadFile(long position) {
        seekMmapFile(position, true);
    }

    @Override
    public int readInt(long position) {
        if (!canRead(position, 4)) {
            return 0;
        }
        seekReadFile(position);
        return readFile.readInt(position % scrollSize);
    }

    @Override
    public long readLong(long position) {
        if (!canRead(position, 8)) {
            return 0;
        }
        seekReadFile(position);
        return readFile.readLong(position % scrollSize);
    }

    @Override
    public byte[] readBytes(long position, int size) {
        if (!canRead(position, size)) {
            return new byte[0];
        }
        seekReadFile(position);
        return readFile.readBytes(position % scrollSize, size);
    }

    @Override
    public <T extends Serializable> T readObject(long position, int size) {
        if (!canRead(position, size)) {
            return null;
        }
        seekReadFile(position);
        return readFile.readObject(position % scrollSize, size);
    }

    @Override
    public <T extends Serializable> Iterable<T> objectIterator(long position) {
        seekReadFile(position);
        return new Iterable<T>() {
            long readPosition = position;
            Iterator<T> iterator = (Iterator<T>) readFile.objectIterator(readPosition % scrollSize).iterator();

            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return readPosition < writePosition;
                    }

                    @Override
                    public T next() {
                        if (iterator.hasNext()) {
                            return iterator.next();
                        } else {
                            readPosition = (readPosition - readPosition % scrollSize) + scrollSize;
                            seekReadFile(readPosition);
                            iterator = (Iterator<T>) readFile.objectIterator(readPosition % scrollSize).iterator();
                            return iterator.next();
                        }
                    }
                };
            }
        };
    }
}
