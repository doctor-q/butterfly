package cc.doctor.wiki.search.server.index.store.indices.writer;

import cc.doctor.wiki.index.document.Document;
import cc.doctor.wiki.index.writer.WriteResult;
import cc.doctor.wiki.search.server.index.store.indices.indexer.IndexerMediator;
import cc.doctor.wiki.search.server.index.store.indices.inverted.InvertedFile;
import cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog.CheckPointFile;
import cc.doctor.wiki.search.server.index.store.source.SourceFile;

import java.util.concurrent.Callable;

/**
 * Created by doctor on 2017/3/8.
 */
public class WriteDocumentCallable implements Callable {
    private IndexerMediator indexerMediator;
    private SourceFile sourceFile;
    private CheckPointFile checkPointFile;
    private Document document;
    private InvertedFile invertedFile;

    public WriteDocumentCallable(final IndexerMediator indexer, final SourceFile sourceFile, final CheckPointFile checkPointFile, InvertedFile invertedFile, final Document document) {
        this.indexerMediator = indexer;
        this.sourceFile = sourceFile;
        this.checkPointFile = checkPointFile;
        this.document = document;
        this.invertedFile = invertedFile;
    }

    @Override
    public Object call() throws Exception {
        //write source
        SourceFile.Source source = new SourceFile.Source();
        source.setDocument(document);
        int position = sourceFile.appendSource(source);
        sourceFile.setPositionById(document.getId(), position);
        //write index
        indexerMediator.index(document);

        return new WriteResult();
    }
}
