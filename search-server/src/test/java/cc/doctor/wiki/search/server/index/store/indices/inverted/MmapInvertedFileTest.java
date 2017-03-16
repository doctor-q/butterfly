package cc.doctor.wiki.search.server.index.store.indices.inverted;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by doctor on 2017/3/15.
 */
public class MmapInvertedFileTest {
    @Test
    public void testMergeTask() {
        MmapInvertedFile mmapInvertedFile = new MmapInvertedFile();
        List<Iterable<InvertedTable.InvertedDoc>> lists = new LinkedList<>();
        long[][] docIds = {{1, 2, 3, 54, 21, 77, 239, 29, 49, 5, 8, 66, 44, 22, 99, 10, 20, 30},
                {1, 2, 3, 54, 21, 77, 239, 29, 49, 5, 8, 66, 44, 22, 99, 10, 20, 30},
                {1, 2, 3, 54, 21, 77, 239, 29, 49, 5, 8, 66, 44, 22, 99, 10, 20, 30},
                {1, 2, 3, 54, 21, 77, 239, 29, 49, 5, 8, 66, 44, 22, 99, 10, 20, 30},
                {1, 2, 3, 54, 21, 77, 239, 29, 49, 5, 8, 66, 44, 22, 99, 10, 20, 30},
                {1, 2, 3, 54, 21, 77, 239, 29, 49, 5, 8, 66, 44, 22, 99, 10, 20, 30},
                {1, 2, 3, 54, 21, 77, 239, 29, 49, 5, 8, 66, 44, 22, 99, 10, 20, 30},
                {1, 2, 3, 54, 21, 77, 239, 29, 49, 5, 8, 66, 44, 22, 99, 10, 20, 30},
                {1, 2, 3, 54, 21, 77, 239, 29, 49, 5, 8, 66, 44, 22, 99, 10, 20, 30},
                {1, 2, 3, 54, 21, 77, 239, 29, 49, 5, 8, 66, 44, 22, 99, 10, 20, 30},
                {1, 2, 3, 54, 21, 77, 239, 29, 49, 5, 8, 66, 44, 22, 99, 10, 20, 30}};
        for (long[] docId : docIds) {
            List<InvertedTable.InvertedDoc> invertedDocs = new LinkedList<>();
            for (long l : docId) {
                invertedDocs.add(new InvertedTable.InvertedDoc(l, 1));
            }
            lists.add(invertedDocs);
        }

    }

}