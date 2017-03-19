package cc.doctor.wiki.search.server.cluster.routing;

import cc.doctor.wiki.ha.zk.ZkEventListenerAdapter;
import org.apache.zookeeper.WatchedEvent;

/**
 * Created by doctor on 2017/3/19.
 * 路由信息监听
 */
public class RoutingNodeListener extends ZkEventListenerAdapter {
    public static final String ZK_ROUTING_NODE = "/es/metadata/routing/nodes";
    @Override
    public void onNodeChildrenChanged(WatchedEvent watchedEvent) {
        super.onNodeChildrenChanged(watchedEvent);
    }
}
