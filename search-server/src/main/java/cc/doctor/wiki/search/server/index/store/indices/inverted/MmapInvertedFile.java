package cc.doctor.wiki.search.server.index.store.indices.inverted;

import cc.doctor.wiki.search.server.index.store.mm.MmapFile;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by doctor on 2017/3/7.
 */
public class MmapInvertedFile extends InvertedFile {
    MmapFile mmapFile;

    public static void main(String[] args) {
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

        Iterable<InvertedTable.InvertedDoc> invertedDocs = mmapInvertedFile.mergeDocs(lists);
        System.out.println(invertedDocs);
    }

    @Override
    public InvertedTable getInvertedTable(WordInfo.InvertedNode invertedNode) {
        return mmapFile.readObject(invertedNode.getPosition(), invertedNode.getSize());
    }

    @Override
    public boolean writeInvertedTable(InvertedTable invertedTable) {
        int length = mmapFile.writeObject(invertedTable);
        return length > 0;
    }
}
