package cc.doctor.search.client.route;

import cc.doctor.search.client.NodeState;
import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by doctor on 2017/3/13.
 * 节点信息和路由信息，当节点不存在相关路由时，routing_shards是空的,根据索引名或别名找到对应的节点
 */
@Data
public class RoutingNode {
    private boolean master;     //是否主节点
    private String host;
    private int port;
    private String nodeId;  //retain
    private String nodeName;
    private NodeState nodeState;
    /**
     * index and shard info
     */
    private Map<String, List<RoutingShard>> routingShards = new HashMap<>();

    public void addRoutingShard(RoutingShard routingShard) {
        List<RoutingShard> routingShards = this.routingShards.get(routingShard.getIndexName());
        if (routingShards == null) {
            routingShards = new LinkedList<>();
            this.routingShards.put(routingShard.getIndexName(), routingShards);
        }
        routingShards.add(routingShard);
    }
}
