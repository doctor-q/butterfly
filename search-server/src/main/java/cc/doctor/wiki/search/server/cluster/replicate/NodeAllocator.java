package cc.doctor.wiki.search.server.cluster.replicate;

import cc.doctor.wiki.common.Tuple;
import cc.doctor.wiki.ha.zk.ZookeeperClient;
import cc.doctor.wiki.search.server.cluster.routing.RoutingNode;
import cc.doctor.wiki.search.server.cluster.routing.RoutingService;
import cc.doctor.wiki.search.server.cluster.routing.RoutingShard;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
        List<RoutingNode> routingNodes = routingService.getRoutingNodes();
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
            RoutingShard routingShard = new RoutingShard(indexName, routingNode.getNodeId(), nodeShard.getT2());
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
