package cc.doctor.wiki.search.server.index.store.indices.inverted;

import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.server.index.manager.IndexManagerInner;
import cc.doctor.wiki.search.server.index.shard.ShardService;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by doctor on 2017/3/15.
 */
public class MmapInvertedFileTest {
    InvertedFile invertedFile;

    @Before
    public void setUp() {
        IndexManagerInner indexManagerInner = new IndexManagerInner(new Schema());
        indexManagerInner.setIndexRoot("/tmp/es/data/order_info");
        ShardService shardService = new ShardService(indexManagerInner, 0);
        invertedFile = new MmapInvertedFile(shardService.getIndexerMediator());
    }

    @Test
    public void writeInvertedTable() {
        long k = 0;
        for (int i = 0; i < 1900; i++) {
            InvertedTable invertedTable = new InvertedTable();
            invertedTable.setField("name");
            invertedTable.setWordInfo(new WordInfo().position(++k));
            List<InvertedTable.InvertedDoc> invertedDocs = new LinkedList<>();
            for (int j = 0; j < 100; j++) {
                invertedDocs.add(new InvertedTable.InvertedDoc(j, 1));
            }
            invertedTable.setInvertedDocs(invertedDocs);
            invertedFile.writeInvertedTable(invertedTable);
        }
        invertedFile.flushInvertedTable();
    }

    @Test
    public void testMergeTask() {
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