package cc.doctor.search.server.index.store.indices.inverted;

import cc.doctor.search.common.entity.Tuple;
import cc.doctor.search.client.index.schema.Schema;
import cc.doctor.search.server.index.manager.IndexManagerInner;
import cc.doctor.search.server.index.shard.ShardService;
import cc.doctor.search.server.index.store.indices.indexer.IndexerMediator;
import cc.doctor.search.server.index.store.indices.indexer.datastruct.TrieTree;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by doctor on 2017/3/30.
 */
public class DictFileTest  {
    Map<String, Serializable> fieldDict = new HashMap<>();
    DictFile dictFile;
    @Before
    public void setUp() {
        ShardService shardService = new ShardService(new IndexManagerInner(new Schema()), 0);
        shardService.setShardRoot("/tmp/es/shard");
        dictFile = new DictFile(new IndexerMediator(shardService));
        //number
        String[] numFields = new String[] {"id", "age", "size", "count"};
        for (String numField : numFields) {
            ConcurrentSkipListMap<Number, Tuple> skipListMap = new ConcurrentSkipListMap<>();
            for (int i = 0; i < 1000; i++) {
                skipListMap.put(i,new Tuple<>(i, ""));
            }
            fieldDict.put(numField, skipListMap);
        }
        //string
        String[] strFields = new String[]{"name", "address", "company", "alias"};
        for (String strField : strFields) {
            TrieTree<Integer> trieTree = new TrieTree<>();
            for (int i = 1000; i < 2000; i++) {
                trieTree.insertWord(String.valueOf(i), i);
            }
            fieldDict.put(strField, trieTree);
        }
    }

    @Test
    public void writeDict() throws Exception {
        dictFile.writeDict(fieldDict);
        dictFile.flushFieldPosition();
    }

    @Test
    public void readFieldPosition() throws Exception {
        dictFile.readFieldPosition();
        HashMap<String, Tuple<Integer, Integer>> fieldPosition = dictFile.getFieldPosition();
        for (String field : fieldPosition.keySet()) {
            System.out.println(field + "=" + fieldPosition.get(field));
        }
    }

    @Test
    public void readDict() throws Exception {
        Map<String, Serializable> dict = dictFile.readDict();
        for (String field : dict.keySet()) {
            System.out.println(field);
            System.out.println(dict.get(field));
        }
    }

}