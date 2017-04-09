package cc.doctor.wiki.search.server.cluster.node.schema;

import cc.doctor.wiki.ha.zk.ZkEventListenerAdapter;
import cc.doctor.wiki.ha.zk.ZookeeperClient;
import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.utils.SerializeUtils;
import org.apache.zookeeper.WatchedEvent;

import java.util.Map;

import static cc.doctor.wiki.search.server.common.Container.container;
import static cc.doctor.wiki.search.server.common.config.Settings.settings;

/**
 * Created by doctor on 2017/4/9.
 */
public class SchemaNodeListener extends ZkEventListenerAdapter {
    public static final String INDEX_SCHEMA_ROOT = settings.getString(GlobalConfig.ZOOKEEPER_INDEX_SCHEMA_PATH);
    private ZookeeperClient zookeeperClient = ZookeeperClient.getClient(settings.getString(GlobalConfig.ZOOKEEPER_CONN_STRING));
    private SchemaService schemaService;
    public SchemaNodeListener() {
        listenPaths.add(INDEX_SCHEMA_ROOT);
        schemaService = container.getComponent(SchemaService.class);
    }

    @Override
    public void onNodeChildrenChanged(WatchedEvent watchedEvent) {
        Map<String, String> indexSchemas = zookeeperClient.getChildren(INDEX_SCHEMA_ROOT);
        for (String index : indexSchemas.keySet()) {
            Schema schema = SerializeUtils.jsonToObject(indexSchemas.get(index), Schema.class);
            schemaService.putSchema(schema);
        }
    }
}
