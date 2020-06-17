package cc.doctor.search.store.mm;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MmapFileTest {
    private MmapFile mmapFile;

    @Before
    public void setUp() throws IOException {
        mmapFile = new MmapFile("/tmp/mmaptest", 10 * 1024 * 1024);
    }

    @Test
    public void test() throws IOException {
        File file = new File("/tmp/mmaptest");
        FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 100);
        byte[] bytes = new byte[10];
        System.out.println(mappedByteBuffer.get(bytes));
        System.out.println(bytes);
    }

    @Test
    public void appendBytes() {
        mmapFile.appendBytes("appendBytes".getBytes());
    }

    @Test
    public void appendInt() {
        mmapFile.appendInt(1001);
    }

    @Test
    public void appendLong() {
        mmapFile.appendLong(100000222L);
    }

    @Test
    public void writeObject() {
        mmapFile.writeObject("writeObject");
    }


    @Test
    public void readBytes() {
        byte[] bytes = mmapFile.readBytes(0, "appendBytes".getBytes().length);
        System.out.println(new String(bytes));
    }

    @Test
    public void readInt() {
        int i = mmapFile.readInt(11);
        System.out.println(i);
    }

    @Test
    public void readLong() {
        System.out.println(mmapFile.readLong(15));
    }

    @Test
    public void objectIterator() {
        mmapFile.setWritePosition(0);
        for (int i = 0; i < 99; i++) {
            mmapFile.writeObject(String.valueOf(i));
        }
        Iterable<String> iterator = mmapFile.objectIterator(0);
        for (String s : iterator) {
            System.out.println(s);
        }
    }

}