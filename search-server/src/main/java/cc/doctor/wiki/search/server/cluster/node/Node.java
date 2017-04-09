package cc.doctor.wiki.search.server.cluster.node;

import cc.doctor.wiki.schedule.Scheduler;
import cc.doctor.wiki.search.server.cluster.node.tolerance.ToleranceService;
import cc.doctor.wiki.search.server.cluster.replicate.ReplicateService;
import cc.doctor.wiki.search.server.cluster.routing.NodeState;
import cc.doctor.wiki.search.server.cluster.routing.RoutingNode;
import cc.doctor.wiki.search.server.cluster.routing.RoutingService;
import cc.doctor.wiki.search.server.cluster.vote.VoteService;
import cc.doctor.wiki.search.server.index.store.indices.recovery.RecoveryService;
import cc.doctor.wiki.search.server.rpc.NettyServer;
import cc.doctor.wiki.search.server.rpc.Server;

import static cc.doctor.wiki.search.server.common.Container.container;

/**
 * Created by doctor on 2017/3/13.
 */
public class Node {

    private RoutingNode routingNode;
    private NodeService nodeService;
    private VoteService voteService;
    private RoutingService routingService;
    private ReplicateService replicateService;
    private ToleranceService toleranceService;
    private NodeClientHolder nodeClientHolder;
    private RecoveryService recoveryService;
    private Server server;
    private Scheduler scheduler;

    public RoutingNode getRoutingNode() {
        return routingNode;
    }

    public void setRoutingNode(RoutingNode routingNode) {
        this.routingNode = routingNode;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public VoteService getVoteService() {
        return voteService;
    }

    public RoutingService getRoutingService() {
        return routingService;
    }

    public Server getServer() {
        return server;
    }

    public Node() {
        nodeClientHolder = new NodeClientHolder(this);
        server = new NettyServer();
        nodeService = new NodeService(this);
        voteService = new VoteService(this);
        routingService = new RoutingService();
        replicateService = new ReplicateService(this);
        toleranceService = new ToleranceService(this);
        recoveryService = new RecoveryService();
        scheduler = new Scheduler();

        container.addComponent(nodeClientHolder);
        container.addComponent(nodeService);
        container.addComponent(voteService);
        container.addComponent(routingService);
        container.addComponent(replicateService);
        container.addComponent(recoveryService);
        container.addComponent(toleranceService);
    }

    public void start() {
        //注册监听对象，监听节点变更和主节点变更
        routingService.registerRoutingNodeListener();
        voteService.registerMasterNodeListener();
        //注册节点
        nodeService.registerNode();
        //选主
        voteService.doVote();
        //生成路由表
        routingService.loadRoutingNodes();
        //恢复数据
        //启动定时任务
        scheduler.scanTasks();
        //启动rpc服务
        server.start();
        //更新节点状态
        nodeService.updateNodeState(NodeState.RUNNING);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                stop();
            }
        }));
    }

    public void stop() {
        //stop node
        nodeService.unregisterNode();
    }

    public static void main(String[] args) {
        new Node().start();
    }
}
