package cc.doctor.wiki.search.server.index.store.indices.writer;

import cc.doctor.wiki.index.document.Document;
import cc.doctor.wiki.operation.Operation;
import cc.doctor.wiki.search.server.index.config.GlobalConfig;
import cc.doctor.wiki.search.server.index.config.PropertyUtils;
import cc.doctor.wiki.search.server.index.store.indices.indexer.IndexerMediator;
import cc.doctor.wiki.search.server.index.store.indices.inverted.InvertedFile;
import cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog.CheckPointFile;
import cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog.OperationLog;
import cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog.OperationLogFile;
import cc.doctor.wiki.search.server.index.store.source.SourceFile;
import cc.doctor.wiki.utils.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by doctor on 2017/3/8.
 * 索引器,负责写文档
 */
public class IndexWriterInner {
    private static final Logger log = LoggerFactory.getLogger(IndexWriterInner.class);
    private static final int writeDocumentThreads = PropertyUtils.getProperty(GlobalConfig.THREAD_NUM_WRITE_DOCUMENT, GlobalConfig.THREAD_NUM_WRITE_DOCUMENT_DEFAULT);
    private IndexerMediator indexer;
    private OperationLogFile operationLogFile;
    private SourceFile sourceFile;
    private CheckPointFile checkPointFile;
    private InvertedFile invertedFile;

    ExecutorService executorService = Executors.newFixedThreadPool(writeDocumentThreads);

    public IndexWriterInner() {
        //初始化所有的组件
    }

    /**
     * 写文档,同步写操作日志,异步建索引,源
     *
     * @param document 文档
     */
    public void writeDocument(Document document) {
        try {
            byte[] bytes = SerializeUtils.serialize(document);
            OperationLog operationLog = new OperationLog();
            operationLog.setOperation(Operation.ADD_DOCUMETN);
            operationLog.setSize(bytes.length);
            operationLog.setData(bytes);
            operationLogFile.appendOperationLog(operationLog);
            executorService.submit(new WriteDocumentCallable(indexer, sourceFile, checkPointFile, invertedFile, document));
        } catch (IOException e) {
            log.error("", e);
        }

    }


}
