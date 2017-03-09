package cc.doctor.wiki.search.server.index.manager;

import cc.doctor.wiki.exceptions.index.IndexException;
import cc.doctor.wiki.index.document.Document;
import cc.doctor.wiki.search.server.index.store.schema.Schema;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by doctor on 2017/3/9.
 */
public class IndexManagerContainer {
    public static final IndexManagerContainer indexManagerContainer = new IndexManagerContainer();
    private Map<String, IndexManagerInner> indexManagerInnerMap = new HashMap<>();

    private IndexManagerContainer() {
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

    public void writeDocument(Schema schema, Document document) {
        if (indexManagerInnerMap.get(schema.getIndexName()) != null) {
            throw new IndexException("Index exists.");
        }
        indexManagerInnerMap.get(schema.getIndexName()).writeDocumentInner(document);
    }
}
