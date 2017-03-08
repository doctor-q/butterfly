package cc.doctor.wiki.search.server.index.store.indices.indexer;

import cc.doctor.wiki.index.document.Document;

/**
 * Created by doctor on 2017/3/8.
 */
public class IndexerMediator {
    public static final IndexerMediator indexerMediator = new IndexerMediator();
    private JumpTableIndexer jumpTableIndexer;
    private TrieTreeIndexer trieTreeIndexer;
    private IndexerMediator() {
        jumpTableIndexer = new JumpTableIndexer();
        trieTreeIndexer = new TrieTreeIndexer();
    }

    //为文档建索引
    public void index(Document document) {

    }
}
