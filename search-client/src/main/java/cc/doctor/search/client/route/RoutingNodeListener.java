package cc.doctor.search.client.route;

import cc.doctor.search.common.ha.zk.ZkEventListenerAdapter;
import cc.doctor.search.common.ha.zk.ZookeeperPaths;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;

import static cc.doctor.search.common.utils.Container.container;

/**
 * Created by doctor on 2017/3/19.
 * 路由信息监听
 */
@Slf4j
public class RoutingNodeListener extends ZkEventListenerAdapter {

    private RoutingService routingService;

    public RoutingNodeListener() {
        listenPaths.add(ZookeeperPaths.NODE_DATA_ROOT);
        routingService = container.getComponent(RoutingService.class);
    }

    @Override
    public void onNodeCreate(WatchedEvent watchedEvent) {
        super.onNodeCreate(watchedEvent);
    }

    @Override
    public void onNodeChildrenChanged(WatchedEvent watchedEvent) {
    }
}
