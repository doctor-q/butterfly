package cc.doctor.search.server.index.manager;

import cc.doctor.search.client.query.QueryBuilder;
import cc.doctor.search.common.document.Document;
import cc.doctor.search.common.entity.Tuple;
import cc.doctor.search.common.exceptions.index.IndexException;
import cc.doctor.search.common.schema.Schema;
import cc.doctor.search.common.utils.CollectionUtils;
import cc.doctor.search.common.utils.FileUtils;
import cc.doctor.search.server.cluster.node.schema.SchemaService;
import cc.doctor.search.server.query.SearcherInner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by doctor on 2017/3/9.
 * manage cluster index
 */
public class IndexManagerService {
    public static final String INDEX_PATH_ROOT = "";
    private SchemaService schemaService;
    private Map<String, IndexManagerInner> indexManagerInnerMap = new HashMap<>();
    private Map<String, SearcherInner> searcherInnerMap = new HashMap<>();

    public IndexManagerService(SchemaService schemaService) {
        this.schemaService = schemaService;
    }

    public void loadIndexes() {
        List<String> indexNames = FileUtils.list(INDEX_PATH_ROOT, CollectionUtils.list("operationlog"));
        for (String indexName : indexNames) {
            indexManagerInnerMap.put(indexName, new IndexManagerInner(schemaService.getSchema(indexName)));
            searcherInnerMap.put(indexName, new SearcherInner(indexName));
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
        if (schema == null) {
            return;
        }
        Schema oldSchema = schemaService.getIndexSchemas().get(schema.getIndexName());
        if (oldSchema != null) {
            //replicate, shards, the exist property can't be update.
            oldSchema.setDynamic(schema.getDynamic());
            oldSchema.setFilters(schema.getFilters());
            oldSchema.setTypeHandlers(schema.getTypeHandlers());
            oldSchema.setTokenizers(schema.getTokenizers());
            for (Schema.Property property : schema.getProperties()) {
                oldSchema.addPropertyIfNotExist(property);
            }
            schemaService.putSchema(oldSchema);
        } else {
            schemaService.getIndexSchemas().put(schema.getIndexName(), schema);
            schemaService.putSchema(schema);
        }
    }

    public void putAlias(Tuple<String, String> alias) {
        if (alias != null) {
            Schema schema = schemaService.getIndexSchemas().get(alias.getT1());
            schema.setAlias(alias.getT2());
            schemaService.putSchema(schema);
        }
    }

    public void dropAlias(Tuple<String, String> alias) {
        if (alias != null) {
            Schema schema = schemaService.getIndexSchemas().get(alias.getT1());
            schema.setAlias(null);
            schemaService.putSchema(schema);
        }
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
