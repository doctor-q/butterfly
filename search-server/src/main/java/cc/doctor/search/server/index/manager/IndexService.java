package cc.doctor.search.server.index.manager;

import cc.doctor.search.common.document.Document;
import cc.doctor.search.common.document.Field;
import cc.doctor.search.common.schema.Schema;
import cc.doctor.search.common.utils.FileUtils;
import cc.doctor.search.common.utils.PropertyUtils;
import cc.doctor.search.server.common.config.GlobalConfig;
import cc.doctor.search.server.index.shard.ShardService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Created by doctor on 2017/3/8.
 * 索引管理,负责本地创建索引,调用shardService进行索引操作,每个索引配置一个
 * execute on data node
 */
@Slf4j
public class IndexService {
    public static final String dataRoot = "/tmp/data";
    public static final int defaultShardsNum = PropertyUtils.getProperty(GlobalConfig.DEFAULT_SHARDS_NUM, GlobalConfig.DEFAULT_SHARDS_NUM_DEFAULT);

    /**
     * index name
     */
    private String index;
    /**
     * index root dir
     */
    private String indexRoot;
    /**
     * num of shards
     */
    private int shards;
    /**
     * shard service map
     */
    private Map<Integer, ShardService> shardServiceMap = new HashMap<>();
    /**
     * index schema
     */
    private Schema schema;

    public IndexService(Schema schema) {
        this.schema = schema;
        this.shards = schema.getShards();
        this.indexRoot = dataRoot + "/" + schema.getIndexName();
        loadShards();
    }

    public void setIndexRoot(String indexRoot) {
        this.indexRoot = indexRoot;
    }

    public String getIndexRoot() {
        return indexRoot;
    }

    public void loadShards() {
        List<String> shards = FileUtils.list(indexRoot, null);
        for (String shard : shards) {
            int shd = Integer.parseInt(shard);
            ShardService shardService = new ShardService(indexRoot + "/" + shard, shd);
            shardService.setSchema(schema);
            shardService.loadIndex();
            shardServiceMap.put(shd, shardService);
        }
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
        for (int i = 0; i < shards; i++) {
            ShardService shardService = new ShardService(indexRoot + "/" + i, i);
            shardServiceMap.put(i, shardService);
        }
        return true;
    }

    public boolean dropIndexInner() {
        return FileUtils.dropDirectory(indexRoot);
    }

    public boolean insertDocument(Document document) {
        shardServiceMap.get(allocateShard(document)).writeDocumentInner(document);
        return true;
    }

    private int allocateShard(Document document) {
        if (document.getId() == null) {
            String id = UUID.randomUUID().toString();
            document.setId(id);
        }
        return document.getId().hashCode() % shards;
    }

    public Schema getSchema() {
        return schema;
    }

    public void flush() {
        for (ShardService shardService : shardServiceMap.values()) {
            shardService.flush();
        }
    }

    /**
     * 批量插入,会先将文档的相同列合并,合并后在建索引
     *
     * @param documents 文档集合
     */
    public boolean bulkInsert(Iterable<Document> documents) {
        Map<Integer, List<Document>> shardDocuments = new HashMap<>();
        Map<Integer, Map<String, Map<Object, Set<String>>>> shardInvertedDocMap = new HashMap<>();
        for (Document document : documents) {
            int shard = allocateShard(document);
            List<Document> documentList = shardDocuments.get(shard);
            Map<String, Map<Object, Set<String>>> invertedDocMap = shardInvertedDocMap.get(shard);
            if (invertedDocMap == null) {
                invertedDocMap = new HashMap<>();
                shardInvertedDocMap.put(shard, invertedDocMap);
                documentList = new LinkedList<>();
                shardDocuments.put(shard, documentList);
            }
            documentList.add(document);
            List<Field> fields = document.getFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                Object value = field.getValue();
                Map<Object, Set<String>> invertedDocInners = invertedDocMap.get(fieldName);
                if (invertedDocInners == null) {
                    invertedDocInners = new HashMap<>();
                    invertedDocMap.put(fieldName, invertedDocInners);
                }
                if (invertedDocInners.get(value) == null) {
                    invertedDocInners.put(value, new HashSet<>());
                }
                invertedDocInners.get(value).add(document.getId());
            }
        }
        for (Integer shard : shardInvertedDocMap.keySet()) {
            shardServiceMap.get(shard).addMergedInvertedDocs(shardInvertedDocMap.get(shard), shardDocuments.get(shard));
        }
        return true;
    }

    public boolean deleteDocument(Long docId) {
        return false;
    }

    public boolean bulkDelete(Iterable<Long> docIds) {
        return false;
    }
}
