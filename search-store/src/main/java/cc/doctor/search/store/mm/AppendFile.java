package cc.doctor.search.store.mm;

import java.io.Serializable;

public interface AppendFile {
    /**
     * close file
     */
    void close();

    /**
     * get write position
     */
    long getWritePosition();

    /**
     * set write position
     */
    void setWritePosition(long writePosition);

    /**
     * append bytes
     */
    void appendBytes(byte[] bytes);

    /**
     * append an int
     */
    void appendInt(int aInt);

    /**
     * append a long
     */
    void appendLong(long aLong);

    /**
     * append a object, return size of bytes
     */
    <T extends Serializable> int writeObject(T serializable);

    /**
     * can append size of bytes
     */
    boolean canAppend(int size);

    /**
     * can read size of bytes
     */
    boolean canRead(long position, int size);

    /**
     * read an int
     */
    int readInt(long position);

    /**
     * read a long
     */
    long readLong(long position);

    /**
     * read size of bytes
     */
    byte[] readBytes(long position, int size);

    /**
     * read an object of size
     */
    <T extends Serializable> T readObject(long position, int size);

    /**
     * iterate objects
     */
    <T extends Serializable> Iterable<T> objectIterator(long position);
}
