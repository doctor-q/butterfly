package cc.doctor.wiki.search.server.cluster.routing;

import java.util.List;

/**
 * Created by doctor on 2017/3/15.
 * 路由服务,根据索引获取对应的节点
 */
public class RoutingService {
    public List<NodeInfo> getNodeInfos(String indexName) {
        return null;
    }

    public class NodeInfo {

        private String nodeId;

        public String getNodeId() {
            return nodeId;
        }
    }
}
