package cc.doctor.search.server.index.manager;

import cc.doctor.search.store.indices.indexer.IndexerMediator;
import cc.doctor.search.store.mm.source.SourceFile;
import cc.doctor.search.common.document.Document;
import cc.doctor.search.common.schema.Schema;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by doctor on 2017/3/8.
 */
public class WriteDocumentCallable implements Callable {
    private IndexerMediator indexerMediator;
    private SourceFile sourceFile;
    private List<Document> documents;
    private Map<String, Map<Object, Set<Long>>> fieldValueDocMap;
    private Schema schema;

    public WriteDocumentCallable(final IndexerMediator indexer,
                                 final SourceFile sourceFile,
                                 final List<Document> documents,
                                 final Map<String, Map<Object, Set<Long>>> fieldValueDocMap,
                                 final Schema schema) {
        this.indexerMediator = indexer;
        this.sourceFile = sourceFile;
        this.documents = documents;
        this.fieldValueDocMap = fieldValueDocMap;
        this.schema = schema;
    }

    /**
     * 索引，如果倒排事先已经建好，则只循环写source
     */
    @Override
    public Object call() throws Exception {
        for (Document document : documents) {
            //write source
            SourceFile.Source source = new SourceFile.Source();
            source.setDocument(document);
            long position = sourceFile.appendSource(source);
            sourceFile.setPositionById(document.getId(), position);
            if (fieldValueDocMap == null) {
                //write index
                indexerMediator.index(document);
            }
        }
        if (fieldValueDocMap != null) {
            indexerMediator.index(fieldValueDocMap);
        }
        return null;
    }
}
