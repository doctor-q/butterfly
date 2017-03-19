package cc.doctor.wiki.search.server.cluster.node;

import cc.doctor.wiki.search.server.cluster.routing.NodeState;
import cc.doctor.wiki.search.server.cluster.routing.RoutingNode;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.utils.PropertyUtils;
import cc.doctor.wiki.utils.StringUtils;

import java.util.List;

/**
 * Created by doctor on 2017/3/13.
 */
public class Node {
    public static final String ZK_CONNECTION_STRING = PropertyUtils.getProperty(GlobalConfig.ZOOKEEPER_CONN_STRING, GlobalConfig.ZOOKEEPER_CONN_STRING_DEFUALT);
    public static final String NODE_NAME = PropertyUtils.getProperty(GlobalConfig.NODE_NAME, null);

    private RoutingNode routingNode;
    private List<LifeCycle> components;

    public RoutingNode getRoutingNode() {
        return routingNode;
    }

    public void setRoutingNode(RoutingNode routingNode) {
        this.routingNode = routingNode;
    }

    public void start() {
        for (LifeCycle component : components) {
            component.onNodeStart();
        }
        //注册节点信息到zookeeper
        registerNodeInfo();
        //选主
        for (LifeCycle component : components) {
            component.onNodeStarted();
        }
    }

    private void registerNodeInfo() {
        RoutingNode routingNode = new RoutingNode();
        routingNode.setNodeId(StringUtils.base64UUid());
        routingNode.setNodeName(NODE_NAME);
        routingNode.setNodeState(NodeState.STARTING);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                //unregister
            }
        }));
    }

    public void stop() {
        for (LifeCycle component : components) {
            component.onNodeStop();
        }
        //stop node
    }
}
