package cc.doctor.search.client.route;

import cc.doctor.search.common.ha.zk.ZookeeperClient;
import cc.doctor.search.common.ha.zk.ZookeeperPaths;
import cc.doctor.search.common.utils.SerializeUtils;

import java.util.*;

/**
 * Created by doctor on 2017/3/15.
 * data nodes and shards routing info, get from master
 */
public class RoutingService {
    /**
     * alias
     * <alias, index list>
     */
    private Map<String, List<String>> aliasIndexMap = new HashMap<>();
    /**
     * data node
     * <data node name, node info>
     */
    private Map<String, RoutingNode> dataNodes = new HashMap<>();
    /**
     * primary shard info
     * <index, primary shards >
     */
    private Map<String, Map<Integer, RoutingShard>> primaryShards = new HashMap<>();
    /**
     * backup shard info
     * <index, backup shards>
     */
    private Map<String, Map<Integer, List<RoutingShard>>> backupShards = new HashMap<>();
    // watch data node change
    private ZookeeperClient zkClient;

    public void registerRoutingNodeListener() {
        zkClient.getZookeeperWatcher().registerListener(RoutingNodeListener.class);
    }

    /**
     * get node and shard info by index name
     */
    public List<RoutingNode> getIndexRoutingNodes(String indexName) {
        List<RoutingNode> indexRoutingNodes = new LinkedList<>();
        if (indexName == null) {
            return indexRoutingNodes;
        }
        for (RoutingNode routingNode : dataNodes.values()) {
            if (routingNode.getRoutingShards() != null && routingNode.getRoutingShards().containsKey(indexName)) {
                indexRoutingNodes.add(routingNode);
            }
        }
        return indexRoutingNodes;
    }

    public Collection<RoutingNode> getDataNodes() {
        return dataNodes.values();
    }

    public RoutingNode getNode(String nodeName) {
        return dataNodes.get(nodeName);
    }

    /**
     * load all data node info
     */
    public void loadRoutingNodes() {
        if (zkClient.existsNode(ZookeeperPaths.NODE_DATA_ROOT)) {
            Map<String, String> children = zkClient.getChildren(ZookeeperPaths.NODE_DATA_ROOT);
            for (Map.Entry<String, String> entry : children.entrySet()) {
                dataNodes.put(entry.getKey(), SerializeUtils.jsonToObject(entry.getValue(), RoutingNode.class));
            }
        }
    }

    public boolean containsIndexNode(RoutingNode routingNode) {
        for (RoutingNode node : dataNodes.values()) {
            if (node.getNodeName().equals(routingNode.getNodeName())) {
                return true;
            }
        }
        return false;
    }

    public RoutingShard primaryShard(String index, int shard) {
        Map<Integer, RoutingShard> shardMap = primaryShards.get(index);
        if (shardMap == null || shardMap.isEmpty()) {
            return null;
        }
        return shardMap.get(shard);
    }

    public int primaryShards(String index) {
        Map<Integer, RoutingShard> shardMap = primaryShards.get(index);
        if (shardMap == null || shardMap.isEmpty()) {
            return 0;
        }
        return shardMap.size();
    }
}
