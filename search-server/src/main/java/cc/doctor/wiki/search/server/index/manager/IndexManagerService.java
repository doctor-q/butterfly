package cc.doctor.wiki.search.server.index.manager;

import cc.doctor.wiki.common.Tuple;
import cc.doctor.wiki.exceptions.index.IndexException;
import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.client.query.QueryBuilder;
import cc.doctor.wiki.search.client.query.document.Document;
import cc.doctor.wiki.search.server.cluster.node.schema.SchemaService;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.search.server.query.SearcherInner;

import java.util.HashMap;
import java.util.Map;

import static cc.doctor.wiki.search.server.common.Container.container;
import static cc.doctor.wiki.search.server.common.config.Settings.settings;

/**
 * Created by doctor on 2017/3/9.
 * 管控所有的操作
 */
public class IndexManagerService {
    public static final String INDEX_PATH_ROOT = settings.getString(GlobalConfig.DATA_PATH);
    private SchemaService schemaService;
    private Map<String, IndexManagerInner> indexManagerInnerMap = new HashMap<>();
    private Map<String, SearcherInner> searcherInnerMap = new HashMap<>();

    private IndexManagerService() {
        schemaService = container.getComponent(SchemaService.class);
    }

    public void loadIndexes() {
        //load schema
        for (String indexName : schemaService.getIndexSchemas().keySet()) {

        }
        //// TODO: 2017/4/9 load index information
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

    public void flush(String indexName) {
        IndexManagerInner indexManager = getIndexManager(indexName);
        indexManager.flush();
    }
}
