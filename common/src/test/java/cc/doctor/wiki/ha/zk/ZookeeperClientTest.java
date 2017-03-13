package cc.doctor.wiki.ha.zk;

import cc.doctor.wiki.utils.PropertyUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by doctor on 2017/3/13.
 */
public class ZookeeperClientTest {
    ZookeeperClient zookeeperClient;
    @Before
    public void setUp() {
        PropertyUtils.setProperty("zk.listener.scan.package", "cc.doctor.wiki.ha.zk");
        zookeeperClient = new ZookeeperClient();
        zookeeperClient.createConnection();
    }

    @After
    public void release() {
        zookeeperClient.releaseConnection();
    }

    @Test
    public void existsNode() throws Exception {
        System.out.println(zookeeperClient.existsNode("/es"));
        System.out.println(zookeeperClient.existsNode("/"));
        zookeeperClient.createPath("/es", "extend-server");
        System.out.println(zookeeperClient.existsNode("/es"));
        System.out.println(zookeeperClient.readData("/es"));
        zookeeperClient.writeData("/es", "es-server");
        zookeeperClient.deleteNode("/es");
        System.out.println(zookeeperClient.existsNode("/es"));
    }

    @Test
    public void createPathRecursion() {
        zookeeperClient.createPathRecursion("/es/data/master", "master");
    }

}