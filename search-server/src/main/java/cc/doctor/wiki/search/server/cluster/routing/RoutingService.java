package cc.doctor.wiki.search.server.cluster.routing;

import cc.doctor.wiki.ha.zk.ZookeeperClient;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.utils.SerializeUtils;

import java.util.LinkedList;
import java.util.List;

import static cc.doctor.wiki.search.server.common.config.Settings.settings;

/**
 * Created by doctor on 2017/3/15.
 * 路由服务,根据索引获取对应的节点
 */
public class RoutingService {
    private List<RoutingNode> routingNodes = new LinkedList<>();
    private ZookeeperClient zkClient = ZookeeperClient.getClient(settings.getString(GlobalConfig.ZOOKEEPER_CONN_STRING));
    public static final String routingPath = GlobalConfig.ZOOKEEPER_ROUTING_PATH;

    public void registerRoutingNodeListener() {
        zkClient.getZookeeperWatcher().registerListener(RoutingNodeListener.class);
    }

    public List<RoutingNode> getIndexRoutingNodes(String indexName) {
        List<RoutingNode> indexRoutingNodes = new LinkedList<>();
        if (indexName == null) {
            return indexRoutingNodes;
        }
        for (RoutingNode routingNode : routingNodes) {
            if (routingNode.getRoutingShards() != null && routingNode.getRoutingShards().containsKey(indexName)) {
                indexRoutingNodes.add(routingNode);
            }
        }
        return indexRoutingNodes;
    }

    public List<RoutingNode> getRoutingNodes() {
        return routingNodes;
    }

    public void updateRoutingNodes(RoutingNode routingNode) {
        RoutingNode node = getNode(routingNode.getNodeName());
        if (node == null) {
            routingNodes.add(routingNode);
        } else {
            node.setNodeName(routingNode.getNodeName());
            node.setNodeState(routingNode.getNodeState());
            node.setMaster(routingNode.isMaster());
            node.setNodeId(routingNode.getNodeId());
            node.setRoutingShards(routingNode.getRoutingShards());
        }
    }

    public RoutingNode getNode(String nodeName) {
        for (RoutingNode routingNode : routingNodes) {
            if (routingNode.getNodeName().equals(nodeName)) {
                return routingNode;
            }
        }
        return null;
    }

    public void loadRoutingNodes() {
        if (zkClient.existsNode(routingPath)) {
            String data = zkClient.readData(routingPath);
            if (data != null) {
                routingNodes = SerializeUtils.jsonToList(data, RoutingNode.class);
            }
        }
    }

    public RoutingNode getMaster() {
        for (RoutingNode routingNode : routingNodes) {
            if (routingNode.isMaster()) {
                return routingNode;
            }
        }
        return null;
    }

    public boolean containsIndexNode(RoutingNode routingNode) {
        for (RoutingNode node : routingNodes) {
            if (node.getNodeName().equals(routingNode.getNodeName())) {
                return true;
            }
        }
        return false;
    }

    //更新索引信息
    public void updateRoutingInfo() {
        String routing = SerializeUtils.objectToJson(routingNodes);
        if (!zkClient.existsNode(routingPath)) {
            zkClient.createPathRecursion(routingPath, routing);
        } else {
            zkClient.writeData(routingPath, routing);
        }
    }

    public void updateRoutingNodes(List<RoutingNode> routingNodes) {
        if (routingNodes == null || routingNodes.size() == 0) {
            return;
        }
        for (RoutingNode routingNode : routingNodes) {
            updateRoutingNodes(routingNode);
        }
        updateRoutingInfo();
    }
}
