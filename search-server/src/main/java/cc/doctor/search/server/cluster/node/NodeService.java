package cc.doctor.search.server.cluster.node;

import cc.doctor.search.server.cluster.routing.NodeState;
import cc.doctor.search.server.cluster.routing.RoutingNode;
import cc.doctor.search.server.cluster.vote.MasterNodeListener;
import cc.doctor.search.server.common.config.GlobalConfig;
import cc.doctor.search.server.common.config.Settings;
import cc.doctor.search.common.ha.zk.ZookeeperClient;
import cc.doctor.search.common.utils.StringUtils;

/**
 * Created by doctor on 2017/3/20.
 * 节点管理,注册节点,节点状态变更
 */
public class NodeService {
    private ZookeeperClient zkClient = ZookeeperClient.getClient((String) Settings.settings.get(GlobalConfig.ZOOKEEPER_CONN_STRING));
    public static final String NODE_PATH = Settings.settings.getString(GlobalConfig.ZOOKEEPER_NODE_PATH);
    private Node node;
    private String nodePath;

    public NodeService(Node node) {
        this.node = node;
    }

    public void registerNode() {
        RoutingNode routingNode = new RoutingNode();
        routingNode.setHost(Settings.settings.getString(GlobalConfig.NETTY_SERVER_HOST));
        routingNode.setPort(Settings.settings.getInt(GlobalConfig.NETTY_SERVER_PORT));
        routingNode.setNodeId(StringUtils.base64UUid());    //retain
        routingNode.setNodeName(Settings.settings.getString(GlobalConfig.NODE_NAME));
        routingNode.setNodeState(NodeState.STARTING);
        nodePath = NODE_PATH + "/" + routingNode.getNodeName();
        if (!zkClient.existsNode(nodePath)) {
            zkClient.createPathRecursion(nodePath, StringUtils.toNameValuePairString(routingNode));
        }
        zkClient.writeData(nodePath, StringUtils.toNameValuePairString(routingNode));
        node.setRoutingNode(routingNode);
    }

    public void unregisterNode() {
        zkClient.deleteNode(NODE_PATH + "/" + node.getRoutingNode().getNodeName());
    }

    public void removeNode(RoutingNode routingNode) {
        if (node.getRoutingNode().isMaster()) {
            zkClient.deleteNode(NODE_PATH + "/" + routingNode.getNodeName());
        }
    }

    public void registerMasterNodeListener() {
        zkClient.getZookeeperWatcher().registerListener(MasterNodeListener.class);
    }

    public boolean updateNodeState(NodeState nodeState) {
        if (!zkClient.existsNode(nodePath)) {
            return false;
        }
        node.getRoutingNode().setNodeState(nodeState);
        zkClient.writeData(nodePath, StringUtils.toNameValuePairString(node.getRoutingNode()));
        return true;
    }
}
