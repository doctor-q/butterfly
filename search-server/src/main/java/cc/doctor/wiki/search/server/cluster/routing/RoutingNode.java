package cc.doctor.wiki.search.server.cluster.routing;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by doctor on 2017/3/13.
 * 路由节点,根据索引名或别名找到对应的节点
 */
public class RoutingNode {
    private boolean master;     //是否主节点
    private String nodeId;
    private String nodeName;
    private NodeState nodeState;
    private Map<String, List<RoutingShard>> routingShards;

    public boolean isMaster() {
        return master;
    }

    public boolean getMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
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
