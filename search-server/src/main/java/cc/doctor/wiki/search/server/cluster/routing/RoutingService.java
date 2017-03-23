package cc.doctor.wiki.search.server.cluster.routing;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by doctor on 2017/3/15.
 * 路由服务,根据索引获取对应的节点
 */
public class RoutingService {
    private List<RoutingNode> routingNodes = new LinkedList<>();

    public List<RoutingNode> getIndexRoutingNodes(String indexName) {
        List<RoutingNode> indexRoutingNodes = new LinkedList<>();
        if (indexName == null) {
            return indexRoutingNodes;
        }
        for (RoutingNode routingNode : routingNodes) {
            if (routingNode.getRoutingShards().containsKey(indexName)) {
                indexRoutingNodes.add(routingNode);
            }
        }
        return indexRoutingNodes;
    }

    public List<RoutingNode> getRoutingNodes() {
        return routingNodes;
    }

    public void updateRoutingNodes(RoutingNode routingNode) {
        RoutingNode node = getNode(routingNode.getNodeId());
        if (node == null) {
            routingNodes.add(routingNode);
        } else {
            node.setNodeName(routingNode.getNodeName());
            node.setNodeState(routingNode.getNodeState());
            node.setMaster(routingNode.isMaster());
        }
    }

    public RoutingNode getNode(String nodeId) {
        for (RoutingNode routingNode : routingNodes) {
            if (routingNode.getNodeId().equals(nodeId)) {
                return routingNode;
            }
        }
        return null;
    }

    public void loadRoutingNodes() {

    }

    public RoutingNode getMaster() {
        for (RoutingNode routingNode : routingNodes) {
            if (routingNode.isMaster()) {
                return routingNode;
            }
        }
        return null;
    }
}
