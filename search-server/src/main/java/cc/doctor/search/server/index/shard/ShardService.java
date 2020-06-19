package cc.doctor.search.server.index.shard;

import cc.doctor.search.client.query.grammar.Predication;
import cc.doctor.search.common.document.Document;
import cc.doctor.search.common.exceptions.query.QueryGrammarException;
import cc.doctor.search.common.schema.Schema;
import cc.doctor.search.common.utils.CollectionUtils;
import cc.doctor.search.common.utils.FileUtils;
import cc.doctor.search.common.utils.PropertyUtils;
import cc.doctor.search.server.common.config.GlobalConfig;
import cc.doctor.search.server.index.manager.WriteDocumentCallable;
import cc.doctor.search.server.query.grammar.GrammarParser;
import cc.doctor.search.store.StoreConfigs;
import cc.doctor.search.store.indices.indexer.IndexerService;
import cc.doctor.search.store.indices.inverted.InvertedTable;
import cc.doctor.search.store.indices.inverted.WordInfo;
import cc.doctor.search.store.source.MmapSourceFile;
import cc.doctor.search.store.source.SourceFile;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by doctor on 2017/3/9.
 * 分片服务,实际是调用索引服务
 * execute on data node
 */
@Slf4j
public class ShardService {
    /**
     * shard id 0 ... n
     */
    private int shard;
    /**
     * is primary shard
     */
    private boolean primary;
    /**
     * shard root
     */
    private String shardRoot;
    /**
     * do real index
     */
    private IndexerService indexerService;
    /**
     * shard source file
     */
    private SourceFile sourceFile;

    /**
     * index schema
     */
    @Setter
    private Schema schema;

    /**
     * write document thread pool
     */
    private static final int writeDocumentThreads = PropertyUtils.getProperty(GlobalConfig.THREAD_NUM_WRITE_DOCUMENT, GlobalConfig.THREAD_NUM_WRITE_DOCUMENT_DEFAULT);
    private ExecutorService shardWriteExecutor = Executors.newFixedThreadPool(writeDocumentThreads);

    public String getShardRoot() {
        return shardRoot;
    }

    public int getShard() {
        return shard;
    }

    public IndexerService getIndexerService() {
        return indexerService;
    }

    public ShardService(String shardRoot, int shard) {
        this.shardRoot = shardRoot;
        this.shard = shard;
        if (!FileUtils.exists(shardRoot)) {
            //分片目录
            FileUtils.createDirectoryRecursion(shardRoot);
        }
        if (!FileUtils.exists(shardRoot + "/" + StoreConfigs.SOURCE_PATH_NAME)) {
            //source目录
            FileUtils.createDirectoryRecursion(shardRoot + "/" + StoreConfigs.SOURCE_PATH_NAME);
        }
        this.indexerService = new IndexerService();
        this.sourceFile = new MmapSourceFile();
    }

    /**
     * 写文档,同步写操作日志,异步建索引,源
     *
     * @param document 文档
     */
    public boolean writeDocumentInner(Document document) {
        shardWriteExecutor.submit(new WriteDocumentCallable(indexerService, sourceFile,
                CollectionUtils.list(document), null, schema));
        return true;
    }

    public Iterable<WordInfo> searchInvertedDocs(GrammarParser.QueryNode queryNode) {
        Predication predication = queryNode.getPredication();
        switch (predication) {
            case EQUAL:
                return indexerService.equalSearch(queryNode.getField(), queryNode.getValue());
            case GREAT_THAN:
                return indexerService.greatThanSearch(queryNode.getField(), queryNode.getValue());
            case GREAT_THAN_EQUAL:
                return indexerService.greatThanEqualSearch(queryNode.getField(), queryNode.getValue());
            case LESS_THAN:
                return indexerService.lessThanSearch(queryNode.getField(), queryNode.getValue());
            case LESS_THAN_EQUAL:
                return indexerService.lessThanEqualSearch(queryNode.getField(), queryNode.getValue());
            case PREFIX:
                return indexerService.prefixSearch(queryNode.getField(), queryNode.getValue());
            case MATCH:
                return indexerService.matchSearch(queryNode.getField(), queryNode.getValue());
        }
        throw new QueryGrammarException("UnSupported predication exception.");
    }

    public Iterable<InvertedTable> getInvertedTables(Iterable<WordInfo> wordInfos) {
        return null;
    }

    public void setShardRoot(String shardRoot) {
        this.shardRoot = shardRoot;
    }

    /**
     * 刷新索引,
     * 1. 刷新倒排,
     * 2. 刷新词典
     */
    public void flush() {
        indexerService.flushInvertedDocs();
        indexerService.flushIndexer();
    }

    /**
     * 写入倒排
     *
     * @param fieldValueDocMap 格式<域，<值，倒排列表>>，已经是合并之后的倒排
     * @param documents        文档列表
     */
    public boolean addMergedInvertedDocs(Map<String, Map<Object, Set<String>>> fieldValueDocMap, List<Document> documents) {
        shardWriteExecutor.submit(new WriteDocumentCallable(indexerService, sourceFile,
                documents, fieldValueDocMap, schema));
        return true;
    }

    /**
     * 加载索引
     */
    public boolean loadIndex() {
        indexerService.loadIndex();
        return false;
    }
}
