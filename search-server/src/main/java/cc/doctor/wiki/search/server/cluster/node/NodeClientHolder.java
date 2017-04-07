package cc.doctor.wiki.search.server.cluster.node;

import cc.doctor.wiki.search.client.rpc.Client;
import cc.doctor.wiki.search.client.rpc.NettyClient;
import cc.doctor.wiki.search.client.rpc.RpcClient;
import cc.doctor.wiki.search.server.cluster.routing.RoutingNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by doctor on 17-4-6.
 * 节点信息交互客户端
 */
public class NodeClientHolder {
    private Map<String, Client> nodeClients = new HashMap<>();
    private Node node;

    public NodeClientHolder(Node node) {
        this.node = node;
    }

    public boolean addNodeClient(RoutingNode routingNode) {
        String nodeName = routingNode.getNodeName();
        if (!nodeClients.containsKey(nodeName)) {
            Client client = new RpcClient(routingNode.getHost() + ":" + routingNode.getPort());
            nodeClients.put(nodeName, client);
            return true;
        }
        return false;
    }

    public boolean addNodeClient(String nodeName, Client client) {
        if (nodeName == null || client == null) {
            return false;
        }
        if (!nodeName.equals(node.getRoutingNode().getNodeName())) {
            nodeClients.put(nodeName, client);
        }
        return true;
    }

    public boolean removeNodeClient(String nodeName) {
        nodeClients.remove(nodeName);
        return true;
    }

    public Client getNodeClient(String nodeName) {
        return nodeClients.get(nodeName);
    }
}
