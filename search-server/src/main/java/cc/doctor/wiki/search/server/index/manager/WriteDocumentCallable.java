package cc.doctor.wiki.search.server.index.manager;

import cc.doctor.wiki.index.document.Document;
import cc.doctor.wiki.index.writer.WriteResult;
import cc.doctor.wiki.search.server.index.store.indices.indexer.IndexerMediator;
import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.server.index.store.mm.source.SourceFile;

import java.util.concurrent.Callable;

/**
 * Created by doctor on 2017/3/8.
 */
public class WriteDocumentCallable implements Callable {
    private IndexerMediator indexerMediator;
    private SourceFile sourceFile;
    private Document document;
    private Schema schema;

    public WriteDocumentCallable(final IndexerMediator indexer, final SourceFile sourceFile, final Document document, final Schema schema) {
        this.indexerMediator = indexer;
        this.sourceFile = sourceFile;
        this.document = document;
        this.schema = schema;
    }

    @Override
    public Object call() throws Exception {
        //write source
        SourceFile.Source source = new SourceFile.Source();
        source.setDocument(document);
        long position = sourceFile.appendSource(source);
        sourceFile.setPositionById(document.getId(), position);
        //write index
        indexerMediator.index(document, schema);

        return new WriteResult();
    }
}
