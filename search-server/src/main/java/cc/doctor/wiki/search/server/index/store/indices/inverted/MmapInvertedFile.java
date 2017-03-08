package cc.doctor.wiki.search.server.index.store.indices.inverted;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by doctor on 2017/3/7.
 */
public class MmapInvertedFile extends InvertedFile {
    @Override
    public Iterable<InvertedDoc> getInvertedDocs(List<Long> positions) {
        return null;
    }

    public static void main(String[] args) {
        MmapInvertedFile mmapInvertedFile = new MmapInvertedFile();
        List<Iterable<InvertedDoc>> lists = new LinkedList<>();
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
            List<InvertedDoc> invertedDocs = new LinkedList<>();
            for (long l : docId) {
                invertedDocs.add(new InvertedDoc(l, 1));
            }
            lists.add(invertedDocs);
        }

        Iterable<InvertedDoc> invertedDocs = mmapInvertedFile.mergeDocs(lists);
        System.out.println(invertedDocs);
    }
}
