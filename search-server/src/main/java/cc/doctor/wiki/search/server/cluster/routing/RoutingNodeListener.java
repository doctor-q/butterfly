package cc.doctor.wiki.search.server.cluster.routing;

import cc.doctor.wiki.ha.zk.ZkEventListenerAdapter;
import cc.doctor.wiki.ha.zk.ZookeeperClient;
import cc.doctor.wiki.search.server.cluster.node.NodeClientHolder;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.utils.StringUtils;
import org.apache.zookeeper.WatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static cc.doctor.wiki.search.server.common.Container.container;
import static cc.doctor.wiki.search.server.common.config.Settings.settings;

/**
 * Created by doctor on 2017/3/19.
 * 路由信息监听
 */
public class RoutingNodeListener extends ZkEventListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(RoutingNodeListener.class);

    public static final String NODE_PATH = (String) settings.get(GlobalConfig.ZOOKEEPER_NODE_PATH);
    private ZookeeperClient zookeeperClient = ZookeeperClient.getClient(settings.getString(GlobalConfig.ZOOKEEPER_CONN_STRING));
    private RoutingService routingService;
    private NodeClientHolder nodeClientHolder;

    public RoutingNodeListener() {
        listenPaths.add(NODE_PATH);
        routingService = container.getComponent(RoutingService.class);
        nodeClientHolder = container.getComponent(NodeClientHolder.class);
    }

    @Override
    public void onNodeCreate(WatchedEvent watchedEvent) {
        super.onNodeCreate(watchedEvent);
    }

    @Override
    public void onNodeChildrenChanged(WatchedEvent watchedEvent) {
        String path = watchedEvent.getPath();
        if (path.equals(NODE_PATH)) {
            Map<String, String> children = zookeeperClient.getChildren(NODE_PATH);
            List<RoutingNode> routingNodes = new LinkedList<>();
            for (String nodeId : children.keySet()) {
                String routingNodeString = children.get(nodeId);
                Map<String, String> nameValuePair = StringUtils.toNameValuePair(routingNodeString);
                RoutingNode routingNode = new RoutingNode();
                routingNode.setNodeId(nameValuePair.get("nodeId"));
                routingNode.setNodeName(nameValuePair.get("nodeName"));
                routingNode.setNodeState(NodeState.getState(nameValuePair.get("nodeState")));
                routingNode.setHost(nameValuePair.get("host"));
                routingNode.setPort(Integer.parseInt(nameValuePair.get("port")));
                routingNodes.add(routingNode);
                if (routingNode.getNodeState().equals(NodeState.RUNNING)) {
                    nodeClientHolder.addNodeClient(routingNode);
                } else {
                    nodeClientHolder.removeNodeClient(routingNode.getNodeName());
                }
            }
            routingService.updateRoutingNodes(routingNodes);
        }
    }
}
