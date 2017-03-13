package cc.doctor.wiki.search.server.cluster.node;

import cc.doctor.wiki.search.server.cluster.routing.RoutingNode;

import java.util.List;

/**
 * Created by doctor on 2017/3/13.
 */
public class Node {
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
        //node start
        for (LifeCycle component : components) {
            component.onNodeStarted();
        }
    }

    public void stop() {
        for (LifeCycle component : components) {
            component.onNodeStop();
        }
        //stop node
    }
}
