package cc.doctor.wiki.search.server.index.manager;

import cc.doctor.wiki.index.document.Document;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.utils.PropertyUtils;
import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.server.index.shard.ShardService;
import cc.doctor.wiki.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by doctor on 2017/3/8.
 * 索引管理,负责本地创建索引,调用shardService进行索引操作,每个索引配置一个
 */
public class IndexManagerInner {
    private static final Logger log = LoggerFactory.getLogger(IndexManagerInner.class);
    public static final String dataRoot = PropertyUtils.getProperty(GlobalConfig.DATA_PATH, GlobalConfig.DATA_PATH_DEFAULT);
    public static final int defaultShardsNum = PropertyUtils.getProperty(GlobalConfig.DEFAULT_SHARDS_NUM, GlobalConfig.DEFAULT_SHARDS_NUM_DEFAULT);

    private String indexRoot;
    private int shards;
    private Map<Integer, ShardService> shardServiceMap = new HashMap<>();
    private Schema schema;

    public IndexManagerInner(Schema schema) {
        this.schema = schema;
    }

    public String getIndexRoot() {
        return indexRoot;
    }

    /**
     * 创建索引相关的目录
     */
    public boolean createIndexInner() {
        if (schema.getIndexName() == null) {
            return false;
        }
        //索引根目录
        indexRoot = dataRoot + "/" + schema.getIndexName();
        FileUtils.createDirectoryRecursion(indexRoot);
        //分片目录,分片设置后不可更改,除非重建索引
        shards = schema.getShards();
        for (int i = 0; i < shards; i++) {
            ShardService shardService = new ShardService(this, i);
            shardServiceMap.put(i, shardService);
        }
        return true;
    }

    public boolean dropIndexInner() {
        return FileUtils.dropDirectory(indexRoot);
    }

    public boolean writeDocumentInner(Document document) {
        if (document.getId() == null) {
            document.setId(DocIdGenerator.docIdGenerator.generateDocId(schema.getIndexName()));
        }
        int shard = (int) (document.getId() % (long) shards);
        shardServiceMap.get(shard).writeDocumentInner(document);
        return true;
    }

    public Schema getSchema() {
        return schema;
    }
}
