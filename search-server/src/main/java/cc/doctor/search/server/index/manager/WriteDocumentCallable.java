package cc.doctor.search.server.index.manager;

import cc.doctor.search.common.document.Document;
import cc.doctor.search.common.schema.Schema;
import cc.doctor.search.store.indices.indexer.IndexerService;
import cc.doctor.search.store.source.Source;
import cc.doctor.search.store.source.SourceFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by doctor on 2017/3/8.
 */
public class WriteDocumentCallable implements Callable {
    private IndexerService indexerService;
    private SourceFile sourceFile;
    private List<Document> documents;
    private Map<String, Map<Object, Set<String>>> fieldValueDocMap;
    private Schema schema;

    public WriteDocumentCallable(final IndexerService indexer,
                                 final SourceFile sourceFile,
                                 final List<Document> documents,
                                 final Map<String, Map<Object, Set<String>>> fieldValueDocMap,
                                 final Schema schema) {
        this.indexerService = indexer;
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
            Source source = new Source();
            source.setDocument(document);
            long position = sourceFile.appendSource(source);
            sourceFile.setPositionById(document.getId(), position);
            if (fieldValueDocMap == null) {
                //write index
                indexerService.index(document);
            }
        }
        if (fieldValueDocMap != null) {
            indexerService.index(fieldValueDocMap);
        }
        return null;
    }
}
