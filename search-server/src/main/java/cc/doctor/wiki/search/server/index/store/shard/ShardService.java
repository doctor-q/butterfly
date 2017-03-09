package cc.doctor.wiki.search.server.index.store.shard;

import cc.doctor.wiki.index.document.Document;
import cc.doctor.wiki.operation.Operation;
import cc.doctor.wiki.search.server.index.config.GlobalConfig;
import cc.doctor.wiki.search.server.index.config.PropertyUtils;
import cc.doctor.wiki.search.server.index.manager.IndexManagerInner;
import cc.doctor.wiki.search.server.index.manager.WriteDocumentCallable;
import cc.doctor.wiki.search.server.index.store.indices.indexer.IndexerMediator;
import cc.doctor.wiki.search.server.index.store.indices.inverted.InvertedFile;
import cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog.CheckPointFile;
import cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog.OperationLog;
import cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog.OperationLogFile;
import cc.doctor.wiki.search.server.index.store.source.SourceFile;
import cc.doctor.wiki.utils.FileUtils;
import cc.doctor.wiki.utils.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by doctor on 2017/3/9.
 * 分片服务,实际是调用索引服务
 */
public class ShardService {
    private static final Logger log = LoggerFactory.getLogger(ShardService.class);
    private int shard;
    private String shardRoot;
    private IndexManagerInner indexManagerInner;
    private IndexerMediator indexerMediator;
    private OperationLogFile operationLogFile;
    private SourceFile sourceFile;
    private CheckPointFile checkPointFile;
    private InvertedFile invertedFile;
    private static final int writeDocumentThreads = PropertyUtils.getProperty(GlobalConfig.THREAD_NUM_WRITE_DOCUMENT, GlobalConfig.THREAD_NUM_WRITE_DOCUMENT_DEFAULT);
    private ExecutorService shardWriteExecutor = Executors.newFixedThreadPool(writeDocumentThreads);

    public ShardService(IndexManagerInner indexManagerInner, int shard) {
        this.indexManagerInner = indexManagerInner;
        this.shard = shard;
        shardRoot = indexManagerInner.getIndexRoot() + "/" + shard;
        //分片目录
        FileUtils.createDirectoryRecursion(shardRoot);
        //操作日志目录
        FileUtils.createDirectoryRecursion(shardRoot + "/" + GlobalConfig.OPERATION_LOG_PATH_NAME);
        //source目录
        FileUtils.createDirectoryRecursion(shardRoot + "/" + GlobalConfig.SOURCE_PATH_NAME);
        //索引目录
        FileUtils.createDirectoryRecursion(shardRoot + "/" + GlobalConfig.INDEX_PATH_NAME);
    }
    /**
     * 写文档,同步写操作日志,异步建索引,源
     *
     * @param document 文档
     */
    public boolean writeDocumentInner(Document document) {
        try {
            byte[] bytes = SerializeUtils.serialize(document);
            OperationLog operationLog = new OperationLog();
            operationLog.setOperation(Operation.ADD_DOCUMENT);
            operationLog.setSize(bytes.length);
            operationLog.setData(bytes);
            boolean appendOperationLog = operationLogFile.appendOperationLog(operationLog);
            if (appendOperationLog) {
                shardWriteExecutor.submit(new WriteDocumentCallable(indexerMediator, sourceFile, document, indexManagerInner.getSchema()));
            }
            return appendOperationLog;
        } catch (IOException e) {
            log.error("", e);
            return false;
        }
    }

}
