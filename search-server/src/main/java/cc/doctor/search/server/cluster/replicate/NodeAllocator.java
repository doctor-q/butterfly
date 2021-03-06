package cc.doctor.search.server.cluster.replicate;

import cc.doctor.search.client.route.RoutingNode;
import cc.doctor.search.client.route.RoutingService;
import cc.doctor.search.client.route.RoutingShard;
import cc.doctor.search.common.entity.Tuple;
import cc.doctor.search.common.ha.zk.ZookeeperClient;

import java.util.*;

/**
 * Created by doctor on 2017/3/19.
 * 节点分配
 */
public class NodeAllocator {
    private RoutingService routingService;
    private ZookeeperClient zkClient;

    public NodeAllocator(RoutingService routingService, ZookeeperClient zkClient) {
        this.routingService = routingService;
        this.zkClient = zkClient;
    }

    /**
     * 分配索引节点和分片,设置元数据
     * @param replicate 副本数目
     * @param shardNum 分片数目
     * @param indexName 索引名
     */
    public void allocateNodes(int replicate, int shardNum, String indexName) {
        List<RoutingNode> indexRoutingNodes = routingService.getIndexRoutingNodes(indexName);
        if (indexRoutingNodes != null && !indexRoutingNodes.isEmpty()) {
            return;
        }

        List<RoutingNode> routingNodes = new ArrayList<>();
        routingNodes.addAll(routingService.getDataNodes());
        int nodeNum = routingNodes.size();
        int signedNodeNum = replicate;
        int unsignedNodeNum = 0;
        if (replicate > nodeNum) {  //副本数大于节点数,分配unsigned节点
            signedNodeNum = nodeNum;
            unsignedNodeNum = replicate - nodeNum;
        }
        List<Tuple<Integer, Integer>> tuples = allocateNodeAndShard(signedNodeNum, shardNum);
        for (Tuple<Integer, Integer> nodeShard : tuples) {
            RoutingNode routingNode = routingNodes.get(nodeShard.getT1());
            RoutingShard routingShard = new RoutingShard(indexName, routingNode.getNodeName(), nodeShard.getT2());
            routingNode.addRoutingShard(routingShard);
        }
        allocateNodeAndShard(unsignedNodeNum, shardNum);
    }

    /**
     * 分配节点和分片的数目
     *
     * @param nodeNum  节点数目
     * @param shardNum 分片数目
     */
    public List<Tuple<Integer, Integer>> allocateNodeAndShard(int nodeNum, int shardNum) {
        List<Tuple<Integer, Integer>> nodeShardTuples = new LinkedList<>();
        if (nodeNum == 0 || shardNum == 0) {
            return nodeShardTuples;
        }
        for (int i = 0; i < shardNum; i++) {
            List<Integer> randomNodes = randomNodes(nodeNum, shardNum);
            for (Integer randomNode : randomNodes) {
                nodeShardTuples.add(new Tuple<>(randomNode, i));
            }
        }
        return nodeShardTuples;
    }

    //从nodeNum中随机取shardNum个
    private List<Integer> randomNodes(int nodeNum, int shardNum) {
        List<Integer> randoms = new LinkedList<>();
        Random random = new Random();
        List<Integer> nodes = new LinkedList<>();
        for (int i = 0; i < nodeNum; i++) {
            nodes.add(i);
        }
        for (int i = 0; i < shardNum; i++) {
            int size = nodes.size();
            int index = random.nextInt() % size;
            randoms.add(nodes.get(index));
        }
        return randoms;
    }
}
