package cc.doctor.wiki.search.server.index.manager;

import cc.doctor.wiki.search.client.query.document.Document;
import cc.doctor.wiki.search.client.query.document.Field;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.utils.PropertyUtils;
import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.server.index.shard.ShardService;
import cc.doctor.wiki.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static cc.doctor.wiki.search.server.common.config.Settings.settings;

/**
 * Created by doctor on 2017/3/8.
 * 索引管理,负责本地创建索引,调用shardService进行索引操作,每个索引配置一个
 */
public class IndexManagerInner {
    private static final Logger log = LoggerFactory.getLogger(IndexManagerInner.class);
    public static final String dataRoot = (String) settings.get(GlobalConfig.DATA_PATH);
    public static final int defaultShardsNum = PropertyUtils.getProperty(GlobalConfig.DEFAULT_SHARDS_NUM, GlobalConfig.DEFAULT_SHARDS_NUM_DEFAULT);

    private String indexRoot;
    private int shards;
    private Map<Integer, ShardService> shardServiceMap = new HashMap<>();
    private Schema schema;

    public IndexManagerInner(Schema schema) {
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

    public boolean loadShards() {
        List<String> shards = FileUtils.list(indexRoot, null);
        for (String shard : shards) {
            int shd = Integer.parseInt(shard);
            ShardService shardService = new ShardService(this, shd);
            shardService.loadIndex();
            shardServiceMap.put(shd, shardService);
        }
        return true;
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
            ShardService shardService = new ShardService(this, i);
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
            document.setId(DocIdGenerator.docIdGenerator.generateDocId(schema.getIndexName()));
        }
        return (int) (document.getId() % (long) shards);
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
     * @param documents 文档集合
     */
    public boolean bulkInsert(Iterable<Document> documents) {
        Map<Integer, List<Document>> shardDocuments = new HashMap<>();
        Map<Integer, Map<String, Map<Object, Set<Long>>>> shardInvertedDocMap = new HashMap<>();
        for (Document document : documents) {
            int shard = allocateShard(document);
            List<Document> documentList = shardDocuments.get(shard);
            Map<String, Map<Object, Set<Long>>> invertedDocMap = shardInvertedDocMap.get(shard);
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
                Map<Object, Set<Long>> invertedDocInners = invertedDocMap.get(fieldName);
                if (invertedDocInners == null) {
                    invertedDocInners = new HashMap<>();
                    invertedDocMap.put(fieldName, invertedDocInners);
                }
                if (invertedDocInners.get(value) == null) {
                    invertedDocInners.put(value, new HashSet<Long>());
                }
                invertedDocInners.get(value).add(document.getId());
            }
        }
        for (Integer shard : shardInvertedDocMap.keySet()) {
            shardServiceMap.get(shard).addInvertedDocs(shardInvertedDocMap.get(shard), shardDocuments.get(shard));
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
