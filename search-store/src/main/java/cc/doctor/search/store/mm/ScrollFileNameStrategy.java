package cc.doctor.search.store.mm;

import java.util.List;

/**
 * scroll strategy
 */
public interface ScrollFileNameStrategy {
    /**
     * the first file name
     */
    String first();

    /**
     * get next file name
     */
    String next(String current);

    /**
     * get file name of global position
     */
    String getFileByPosition(long position, int scrollSize);

    /**
     * the last file
     * return null if files is empty
     */
    String last(List<String> files);
}
