package cc.doctor.search.store.mm;

import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.*;

public class MmapScrollFileTest {
    private MmapScrollFile mmapScrollFile;

    @Before
    public void setUp() {
        mmapScrollFile = new MmapScrollFile("/tmp/mmapscroll", 1024);
    }

    @Test
    public void appendBytes() {
        for (int i = 0; i < 100; i++) {
            mmapScrollFile.appendBytes("appendBytes".getBytes());
        }
    }

    @Test
    public void appendInt() {
        mmapScrollFile.appendInt(1001);
    }

    @Test
    public void appendLong() {
        mmapScrollFile.appendLong(10021122L);
    }

    @Test
    public void writeObject() {
        mmapScrollFile.setWritePosition(1024);
        for (int i = 0; i < 200; i++) {
            mmapScrollFile.writeObject(String.valueOf(i));
        }
    }

    @Test
    public void readInt() {
    }

    @Test
    public void readLong() {
    }

    @Test
    public void readBytes() {
    }

    @Test
    public void readObject() {
    }

    @Test
    public void objectIterator() {
        Iterable<Serializable> serializables = mmapScrollFile.objectIterator(1024);
        for (Serializable serializable : serializables) {
            System.out.println(serializable);
        }
    }
}