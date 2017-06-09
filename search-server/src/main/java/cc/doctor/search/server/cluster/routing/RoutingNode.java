package cc.doctor.search.server.cluster.routing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by doctor on 2017/3/13.
 * 节点信息和路由信息，当节点不存在相关路由时，routing_shards是空的,根据索引名或别名找到对应的节点
 */
public class RoutingNode {
    private boolean master;     //是否主节点
    private String host;
    private int port;
    private String nodeId;  //retain
    private String nodeName;
    private NodeState nodeState;
    private Map<String, List<RoutingShard>> routingShards = new HashMap<>();

    public boolean isMaster() {
        return master;
    }

    public boolean getMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public NodeState getNodeState() {
        return nodeState;
    }

    public void setNodeState(NodeState nodeState) {
        this.nodeState = nodeState;
    }

    public Map<String, List<RoutingShard>> getRoutingShards() {
        return routingShards;
    }

    public void setRoutingShards(Map<String, List<RoutingShard>> routingShards) {
        this.routingShards = routingShards;
    }

    public void addRoutingShard(RoutingShard routingShard) {
        List<RoutingShard> routingShards = this.routingShards.get(routingShard.getIndexName());
        if (routingShards == null) {
            routingShards = new LinkedList<>();
            this.routingShards.put(routingShard.getIndexName(), routingShards);
        }
        routingShards.add(routingShard);
    }
}
