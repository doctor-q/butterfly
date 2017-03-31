package cc.doctor.wiki.search.server.index.manager;

import cc.doctor.wiki.common.Tuple;
import cc.doctor.wiki.exceptions.index.IndexException;
import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.client.query.QueryBuilder;
import cc.doctor.wiki.search.client.query.document.Document;
import cc.doctor.wiki.search.server.index.store.indices.recovery.RecoveryService;
import cc.doctor.wiki.search.server.query.SearcherInner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by doctor on 2017/3/9.
 * 管控所有的操作
 */
public class IndexManagerContainer {
    public static final IndexManagerContainer indexManagerContainer = new IndexManagerContainer();
    private Map<String, IndexManagerInner> indexManagerInnerMap = new HashMap<>();
    private Map<String, SearcherInner> searcherInnerMap = new HashMap<>();

    private IndexManagerContainer() {
    }

    private IndexManagerInner getIndexManager(String indexName) {
        if (indexName == null) {
            throw new IndexException("Null index name.");
        }
        if (indexManagerInnerMap.get(indexName) == null) {
            throw new IndexException("Index dons't exist.");
        }
        return indexManagerInnerMap.get(indexName);
    }

    public void createIndex(Schema schema) {
        if (indexManagerInnerMap.get(schema.getIndexName()) != null) {
            throw new IndexException("Index exists.");
        }
        IndexManagerInner indexManagerInner = new IndexManagerInner(schema);
        indexManagerInnerMap.put(schema.getIndexName(), indexManagerInner);
        indexManagerInner.createIndexInner();
    }

    public void dropIndex(Schema schema) {
        if (indexManagerInnerMap.get(schema.getIndexName()) == null) {
            throw new IndexException("Index doesn't exist.");
        }
        boolean dropIndexState = indexManagerInnerMap.get(schema.getIndexName()).dropIndexInner();
        if (dropIndexState) {
            indexManagerInnerMap.remove(schema.getIndexName());
        }
    }

    public void putSchema(Schema schema) {
    }

    public void putAlias(Tuple<String, String> alias) {
    }

    public void dropAlias(Tuple<String, String> alias) {
    }

    public void insertDocument(String indexName, Document document) {
        IndexManagerInner indexManager = getIndexManager(indexName);
        indexManager.insertDocument(document);
    }

    public void bulkInsert(String indexName, Iterable<Document> documents) {
        IndexManagerInner indexManager = getIndexManager(indexName);
        indexManager.bulkInsert(documents);
    }

    public void deleteDocument(String indexName, Long docId) {
        IndexManagerInner indexManager = getIndexManager(indexName);
        indexManager.deleteDocument(docId);
    }

    public void bulkDelete(String indexName, Iterable<Long> docIds) {
        IndexManagerInner indexManager = getIndexManager(indexName);
        indexManager.bulkDelete(docIds);
    }

    public void deleteByQuery(String indexName, QueryBuilder queryBuilder) {
    }

    public void query(String indexName, QueryBuilder queryBuilder) {
        SearcherInner searcherInner = searcherInnerMap.get(indexName);
        searcherInner.query(queryBuilder);
        //// TODO: 2017/3/16 算分
    }

}
