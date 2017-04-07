package cc.doctor.wiki.search.server.cluster.node;

import cc.doctor.wiki.ha.zk.ZookeeperClient;
import cc.doctor.wiki.search.server.cluster.routing.NodeState;
import cc.doctor.wiki.search.server.cluster.routing.RoutingNode;
import cc.doctor.wiki.search.server.cluster.vote.MasterNodeListener;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.utils.NetworkUtils;
import cc.doctor.wiki.utils.StringUtils;

import static cc.doctor.wiki.search.server.common.config.Settings.settings;

/**
 * Created by doctor on 2017/3/20.
 */
public class NodeService {
    private ZookeeperClient zkClient = ZookeeperClient.getClient((String) settings.get(GlobalConfig.ZOOKEEPER_CONN_STRING));
    public static final String NODE_PATH = settings.getString(GlobalConfig.ZOOKEEPER_NODE_PATH);
    private Node node;
    private String nodePath;

    public NodeService(Node node) {
        this.node = node;
    }

    public void registerNode() {
        RoutingNode routingNode = new RoutingNode();
        routingNode.setHost(settings.getString(GlobalConfig.NETTY_SERVER_HOST));
        routingNode.setPort(settings.getInt(GlobalConfig.NETTY_SERVER_PORT));
        routingNode.setNodeId(StringUtils.base64UUid());    //retain
        routingNode.setNodeName(settings.getString(GlobalConfig.NODE_NAME));
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
