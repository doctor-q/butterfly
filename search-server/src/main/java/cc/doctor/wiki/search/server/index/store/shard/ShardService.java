package cc.doctor.wiki.search.server.index.store.shard;

import cc.doctor.wiki.exceptions.query.QueryGrammarException;
import cc.doctor.wiki.index.document.Document;
import cc.doctor.wiki.search.client.query.grammar.Predication;
import cc.doctor.wiki.search.client.rpc.operation.Operation;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.search.server.index.store.indices.inverted.WordInfo;
import cc.doctor.wiki.search.server.query.grammar.GrammarParser;
import cc.doctor.wiki.utils.PropertyUtils;
import cc.doctor.wiki.search.server.index.manager.IndexManagerInner;
import cc.doctor.wiki.search.server.index.manager.WriteDocumentCallable;
import cc.doctor.wiki.search.server.index.store.indices.indexer.IndexerMediator;
import cc.doctor.wiki.search.server.index.store.indices.recovery.RecoveryService;
import cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog.OperationLog;
import cc.doctor.wiki.search.server.index.store.mm.source.MmapSourceFile;
import cc.doctor.wiki.search.server.index.store.mm.source.SourceFile;
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
    private SourceFile sourceFile;
    private RecoveryService recoveryService;
    private static final int writeDocumentThreads = PropertyUtils.getProperty(GlobalConfig.THREAD_NUM_WRITE_DOCUMENT, GlobalConfig.THREAD_NUM_WRITE_DOCUMENT_DEFAULT);
    private ExecutorService shardWriteExecutor = Executors.newFixedThreadPool(writeDocumentThreads);

    public String getShardRoot() {
        return shardRoot;
    }

    public ShardService(IndexManagerInner indexManagerInner, int shard) {
        this.indexManagerInner = indexManagerInner;
        this.shard = shard;
        shardRoot = indexManagerInner.getIndexRoot() + "/" + shard;
        //分片目录
        FileUtils.createDirectoryRecursion(shardRoot);
        //操作日志目录
        FileUtils.createDirectoryRecursion(shardRoot + "/" + GlobalConfig.OPERATION_LOG_PATH_NAME);
        recoveryService = new RecoveryService(this);
        //source目录
        FileUtils.createDirectoryRecursion(shardRoot + "/" + GlobalConfig.SOURCE_PATH_NAME);
        sourceFile = new MmapSourceFile();
        //索引目录,持久化
        // 正向:文档-词典
        // 泛型:序列化的索引,倒排文件
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
            boolean appendOperationLog = recoveryService.appendOperationLog(operationLog);
            if (appendOperationLog) {
                shardWriteExecutor.submit(new WriteDocumentCallable(indexerMediator, sourceFile, document, indexManagerInner.getSchema()));
            }
            return appendOperationLog;
        } catch (IOException e) {
            log.error("", e);
            return false;
        }
    }

    public Iterable<WordInfo> searchInvertedDocs(GrammarParser.QueryNode queryNode) {
        Predication predication = queryNode.getPredication();
        switch (predication) {
            case EQUAL:
                return indexerMediator.equalSearch(queryNode.getField(), queryNode.getValue());
            case GREAT_THAN:
                return indexerMediator.greatThanSearch(queryNode.getField(), queryNode.getValue());
            case GREAT_THAN_EQUAL:
                return indexerMediator.greatThanEqualSearch(queryNode.getField(), queryNode.getValue());
            case LESS_THAN:
                return indexerMediator.lessThanSearch(queryNode.getField(), queryNode.getValue());
            case LESS_THAN_EQUAL:
                return indexerMediator.lessThanEqualSearch(queryNode.getField(), queryNode.getValue());
            case PREFIX:
                return indexerMediator.prefixSearch(queryNode.getField(), queryNode.getValue());
            case MATCH:
                return indexerMediator.matchSearch(queryNode.getField(), queryNode.getValue());
        }
        throw new QueryGrammarException("UnSupported predication exception.");
    }

}
