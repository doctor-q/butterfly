package cc.doctor.search.store.mm;

import cc.doctor.search.common.utils.FileUtils;
import cc.doctor.search.common.utils.SerializeUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by doctor on 2017/3/7.
 * MMap文件操作
 */
@Slf4j
public class MmapFile implements AppendFile {
    /**
     * mmap 文件大小
     */
    private int fileSize;
    private MappedByteBuffer mappedByteBuffer;
    private FileChannel fileChannel;
    private File file;
    //当前写的地址
    private int writePosition;
    // 读写
    private String mode = "rw";
    /**
     * 总的mmap文件内存字节数
     */
    private static final AtomicLong totalMappedVirtualMemory = new AtomicLong(0);
    /**
     * 总的mmap文件数量
     */
    private static final AtomicInteger totalMappedFiles = new AtomicInteger(0);

    /**
     * create mmap file and set write position
     */
    public MmapFile(String fileName, int size, int writePosition) throws IOException {
        this(fileName, size);
        this.writePosition = writePosition;
        mappedByteBuffer.position(writePosition);
    }

    public long getWritePosition() {
        return writePosition;
    }

    public void setWritePosition(long writePosition) {
        this.writePosition = (int) writePosition;
    }

    public void setWritePosition(int writePosition) {
        mappedByteBuffer.position(writePosition);
        this.writePosition = writePosition;
    }

    public File getFile() {
        return file;
    }

    /**
     * create mmap file, get write position from metadata
     */
    public MmapFile(String file, int fileSize) throws IOException {
        this.file = new File(file);
        this.fileSize = fileSize;
        if (!this.file.exists()) {
            FileUtils.createFileRecursion(file);
            writePosition = 0;
        } else {
            writePosition = ByteBuffer.wrap((byte[]) Files.getAttribute(this.file.toPath(), "user:size")).getInt();
        }
        fileChannel = new RandomAccessFile(file, "rw").getChannel();
        try {
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
            mappedByteBuffer.position(writePosition);
        } catch (IOException e) {
            log.error("", e);
            throw e;
        }
        totalMappedFiles.incrementAndGet();
        totalMappedVirtualMemory.addAndGet(this.fileSize);
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    //刷盘
    public void commit() {
        mappedByteBuffer.force();
    }

    public void close() {
        commit();
        try {
            fileChannel.close();
            Files.setAttribute(file.toPath(), "user:size", ByteBuffer.allocate(4).putInt(writePosition).position(0));
            totalMappedFiles.decrementAndGet();
            totalMappedVirtualMemory.addAndGet(-fileSize);
        } catch (IOException e) {
            log.error("", e);
        }
    }

    public void appendBytes(byte[] bytes) {
        mappedByteBuffer.position(this.writePosition);
        mappedByteBuffer.put(bytes);
        writePosition += bytes.length;
    }

    public void appendInt(int aInt) {
        mappedByteBuffer.position(this.writePosition);
        mappedByteBuffer.putInt(aInt);
        writePosition += 4;
    }

    public void appendLong(long aLong) {
        mappedByteBuffer.position(this.writePosition);
        mappedByteBuffer.putLong(aLong);
        writePosition += 8;
    }

    /**
     * write an object
     * first write size then write object bytes
     *
     * @param serializable object to serialize
     * @return object size of bytes
     */
    public <T extends Serializable> int writeObject(T serializable) {
        try {
            byte[] bytes = SerializeUtils.serialize(serializable);
            appendInt(bytes.length);
            appendBytes(bytes);
            return bytes.length;
        } catch (IOException e) {
            log.error("", e);
        }
        return 0;
    }

    public boolean canAppend(int size) {
        return writePosition + size <= fileSize;
    }

    public boolean canRead(long position, int size) {
        return position + size <= fileSize;
    }

    public int readInt(long position) {
        mappedByteBuffer.position((int) position);
        return mappedByteBuffer.getInt();
    }

    public long readLong(long position) {
        mappedByteBuffer.position((int) position);
        return mappedByteBuffer.getLong();
    }

    /**
     * 从指定位置读取指定大小的byte
     */
    public byte[] readBytes(long position, int size) {
        mappedByteBuffer.position((int) position);
        byte[] bytes = new byte[size];
        mappedByteBuffer.get(bytes);
        return bytes;
    }

    public <T extends Serializable> T readObject(long position, int size) {
        byte[] bytes = readBytes(position, size);
        try {
            return SerializeUtils.deserialize(bytes);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public <T extends Serializable> T readObject(long position) {
        int size = readInt(position);
        return readObject(position + 4, size);
    }

    public <T extends Serializable> Iterable<T> objectIterator(long position) {
        return new Iterable<T>() {
            int readPosition = (int) position;

            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return readPosition < writePosition;
                    }

                    @Override
                    public T next() {
                        int size = readInt(readPosition);
                        T serializable = readObject(readPosition + 4, size);
                        readPosition += (4 + size);
                        return serializable;
                    }
                };
            }
        };
    }

    public String getName() {
        return file.getName();
    }
}
