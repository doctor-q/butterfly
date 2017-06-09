package cc.doctor.search.server.cluster.node.schema;

import cc.doctor.search.server.common.config.Settings;
import cc.doctor.search.common.ha.zk.ZookeeperClient;
import cc.doctor.search.client.index.schema.Schema;
import cc.doctor.search.server.common.config.GlobalConfig;
import cc.doctor.search.common.utils.SerializeUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by doctor on 2017/4/9.
 * schema管理
 */
public class SchemaService {
    private ZookeeperClient zkClient = ZookeeperClient.getClient((String) Settings.settings.get(GlobalConfig.ZOOKEEPER_CONN_STRING));
    public static final String INDEX_SCHEMA_ROOT = Settings.settings.getString(GlobalConfig.ZOOKEEPER_INDEX_SCHEMA_PATH);
    private Map<String, Schema> indexSchemas = new LinkedHashMap<>();

    public Map<String, Schema> getIndexSchemas() {
        return indexSchemas;
    }

    public void registerSchemaNodeListener() {
        zkClient.getZookeeperWatcher().registerListener(SchemaNodeListener.class);
    }

    public boolean putSchema(Schema schema) {
        if (schema == null) {
            return false;
        }
        indexSchemas.put(schema.getIndexName(), schema);
        String json = SerializeUtils.objectToJson(schema);
        String schemaPath = INDEX_SCHEMA_ROOT + "/" + schema.getIndexName();
        if (zkClient.existsNode(schemaPath)) {
            String schemaStr = zkClient.readData(schemaPath);
            Schema oldSchema = SerializeUtils.jsonToObject(schemaStr, Schema.class);
            if (schema.getVersion() > oldSchema.getVersion()) {
                zkClient.writeData(schemaPath, json);
            }
        } else {
            zkClient.createPathRecursion(schemaPath, json);
        }
        return true;
    }

    public boolean loadSchemas() {
        if (zkClient.existsNode(INDEX_SCHEMA_ROOT)) {
            Map<String, String> indexSchemas = zkClient.getChildren(INDEX_SCHEMA_ROOT);
            for (String index : indexSchemas.keySet()) {
                this.indexSchemas.put(index, SerializeUtils.jsonToObject(indexSchemas.get(index), Schema.class));
            }
        } else {
            zkClient.createPathRecursion(INDEX_SCHEMA_ROOT, null);
        }
        return true;
    }

    public Schema getSchema(String indexName) {
        return indexSchemas.get(indexName);
    }
}
