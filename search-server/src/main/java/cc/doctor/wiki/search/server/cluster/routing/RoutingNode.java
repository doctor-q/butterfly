package cc.doctor.wiki.search.server.cluster.routing;

import java.util.List;

/**
 * Created by doctor on 2017/3/13.
 * 路由节点,根据索引名或别名找到对应的节点
 */
public class RoutingNode {
    private boolean master;     //是否主节点
    private String nodeId;
    private String nodeName;
    private NodeState nodeState;
    private List<RoutingShard> routingShards;

    public boolean isMaster() {
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

    public List<RoutingShard> getRoutingShards() {
        return routingShards;
    }

    public void setRoutingShards(List<RoutingShard> routingShards) {
        this.routingShards = routingShards;
    }
}
