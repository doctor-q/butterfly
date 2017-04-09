package cc.doctor.wiki.search.server.cluster.node.schema;

import cc.doctor.wiki.ha.zk.ZookeeperClient;
import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.utils.SerializeUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static cc.doctor.wiki.search.server.common.config.Settings.settings;

/**
 * Created by doctor on 2017/4/9.
 */
public class SchemaService {
    private ZookeeperClient zkClient = ZookeeperClient.getClient((String) settings.get(GlobalConfig.ZOOKEEPER_CONN_STRING));
    public static final String INDEX_SCHEMA_ROOT = settings.getString(GlobalConfig.ZOOKEEPER_INDEX_SCHEMA_PATH);
    private Map<String, Schema> indexSchemas = new LinkedHashMap<>();

    public Map<String, Schema> getIndexSchemas() {
        return indexSchemas;
    }

    public boolean putSchema(Schema schema) {
        if (schema == null) {
            return false;
        }
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
}
