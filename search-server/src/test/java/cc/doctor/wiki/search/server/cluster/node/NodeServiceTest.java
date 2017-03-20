package cc.doctor.wiki.search.server.cluster.node;

import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.utils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by doctor on 2017/3/20.
 */
public class NodeServiceTest {
    NodeService nodeService;
    Node node = new Node();

    @Before
    public void setUp() {
        PropertyUtils.setProperty(GlobalConfig.NODE_NAME, "master");
        nodeService = new NodeService(node);
    }
    @Test
    public void registerNode() throws Exception {

        nodeService.registerNode();
        System.out.println(node.getRoutingNode());
    }

    @Test
    public void unregisterNode() throws Exception {
        registerNode();
        nodeService.unregisterNode();
    }

}