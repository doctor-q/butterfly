package cc.doctor.wiki.search.server.cluster.routing;

import cc.doctor.wiki.ha.zk.ZkEventListenerAdapter;
import cc.doctor.wiki.ha.zk.ZookeeperClient;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.utils.StringUtils;
import org.apache.zookeeper.WatchedEvent;

import java.util.Map;

import static cc.doctor.wiki.search.server.common.config.Settings.settings;

/**
 * Created by doctor on 2017/3/19.
 * 路由信息监听
 */
public class RoutingNodeListener extends ZkEventListenerAdapter {
    public static final String NODE_PATH = (String) settings.get(GlobalConfig.ZOOKEEPER_NODE_PATH);
    private RoutingService routingService;

    @Override
    public void onNodeChildrenChanged(WatchedEvent watchedEvent) {
        ZookeeperClient zookeeperClient = ZookeeperClient.getClient((String) settings.get(GlobalConfig.ZOOKEEPER_CONN_STRING));
        String path = watchedEvent.getPath();
        if (path.equals(NODE_PATH)) {
            Map<String, String> children = zookeeperClient.getChildren(NODE_PATH);
            for (String nodeId : children.keySet()) {
                String routingNodeString = children.get(nodeId);
                Map<String, String> nameValuePair = StringUtils.toNameValuePair(routingNodeString);
                RoutingNode routingNode = new RoutingNode();
                routingNode.setNodeId(nameValuePair.get("nodeId"));
                routingNode.setNodeName(nameValuePair.get("nodeName"));
                routingNode.setNodeState(NodeState.getState(nameValuePair.get("nodeState")));
                routingService.updateRoutingNodes(routingNode);
            }
        }
    }
}
