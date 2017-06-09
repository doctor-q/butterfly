package cc.doctor.search.server.cluster.routing;

import cc.doctor.search.common.ha.zk.ZookeeperClient;
import org.junit.Test;

/**
 * Created by doctor on 2017/3/23.
 */
public class RoutingNodeListenerTest {
    @Test
    public void onNodeChildrenChanged() throws Exception {
        ZookeeperClient zookeeperClient = new ZookeeperClient("127.0.0.1:2181", 10000);
        zookeeperClient.getZookeeperWatcher().registerListener(RoutingNodeListener.class);
        ZookeeperClient client = ZookeeperClient.getClient("127.0.0.1:2181");
        if (!client.existsNode("/es/metadata/nodes/master")) {
            client.createPathRecursion("/es/metadata/nodes/master", "12345");
        }
        client.writeData("/es/metadata/nodes/master", "12345567");
        while (true) {
            Thread.sleep(10000);
        }
    }

}