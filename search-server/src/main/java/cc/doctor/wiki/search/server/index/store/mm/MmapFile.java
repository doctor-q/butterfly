package cc.doctor.wiki.search.server.index.store.mm;

import cc.doctor.wiki.utils.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by doctor on 2017/3/7.
 * MMap文件操作
 */
public class MmapFile {
    private static final Logger log = LoggerFactory.getLogger(MmapFile.class);
    private long fileSize;
    private MappedByteBuffer mappedByteBuffer;
    private FileChannel fileChannel;
    private File file;
    private int position;   //当前写的地址
    private static final AtomicLong totalMappedVirtualMemory = new AtomicLong(0);
    private static final AtomicInteger totalMappedFiles = new AtomicInteger(0);

    public MmapFile(String fileName, int size, int position) throws IOException {
        this(fileName, size);
        this.position = position;
        mappedByteBuffer.position(position);
    }

    public int getPosition() {
        return position;
    }

    public MmapFile(String file, long fileSize) throws IOException {
        this.file = new File(file);
        this.fileSize = fileSize;
        if (this.file.exists()) {
            fileChannel = new RandomAccessFile(file, "rw").getChannel();
        } else {
            throw new FileNotFoundException();
        }
        try {
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
        } catch (IOException e) {
            log.error("", e);
            throw e;
        }
        totalMappedFiles.incrementAndGet();
        totalMappedVirtualMemory.addAndGet(this.fileSize);
    }

    public boolean appendBytes(byte[] bytes) {
        if (position + bytes.length <= fileSize) {
            mappedByteBuffer.position(this.position);
            mappedByteBuffer.put(bytes);
            position += bytes.length;
            return true;
        }
        return false;
    }

    //刷盘
    public void commit() {
        mappedByteBuffer.force();
    }

    /**
     * 从指定位置读取指定大小的byte
     */
    public byte[] readBytes(int position, int size) {
        if (position + size <= fileSize) {
            mappedByteBuffer.position(position);
            ByteBuffer byteBuffer = mappedByteBuffer.slice();
            byte[] bytes = new byte[size];
            byteBuffer.get(bytes);
            return bytes;
        }
        return null;
    }

    public void clean() {
        try {
            fileChannel.close();
            totalMappedFiles.decrementAndGet();
            totalMappedVirtualMemory.addAndGet(-fileSize);
        } catch (IOException e) {
            log.error("", e);
        }
    }

    public static void main(String[] args) throws IOException {
        MmapFile mmapFile = new MmapFile("/tmp/test.txt", 20);
        mmapFile.appendBytes("abcdefghijklmn".getBytes());
        mmapFile.commit();
        byte[] bytes = mmapFile.readBytes(10, 10);
        System.out.println(new String(bytes));
        mmapFile.appendBytes("opqrst".getBytes());
        mmapFile.commit();
        mmapFile.clean();
    }

    public <T extends Serializable> int writeObject(T serializable) {
        try {
            byte[] bytes = SerializeUtils.serialize(serializable);
            appendBytes(bytes);
            return bytes.length;
        } catch (IOException e) {
            log.error("", e);
        }
        return 0;
    }

    public <T extends Serializable> T readObject(int position, int size) {
        byte[] bytes = readBytes(position, size);
        try {
            return SerializeUtils.deserialize(bytes);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public void appendInt(int aInt) {
        mappedByteBuffer.putInt(aInt);
    }

    public void appendLong(long aLong) {
        mappedByteBuffer.putLong(aLong);
    }

    public boolean canAppend(int size) {
        return position + size <= fileSize;
    }

    public int readInt(int position) {
        mappedByteBuffer.position(position);
        return mappedByteBuffer.getInt();
    }

    public long readLong(int position) {
        mappedByteBuffer.position(position);
        return mappedByteBuffer.getLong();
    }
}
