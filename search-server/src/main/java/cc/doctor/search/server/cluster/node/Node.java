package cc.doctor.search.server.cluster.node;

import cc.doctor.search.common.schedule.Scheduler;
import cc.doctor.search.server.cluster.node.schema.SchemaService;
import cc.doctor.search.server.cluster.node.tolerance.ToleranceService;
import cc.doctor.search.server.cluster.replicate.ReplicateService;
import cc.doctor.search.client.NodeState;
import cc.doctor.search.client.route.RoutingNode;
import cc.doctor.search.client.route.RoutingService;
import cc.doctor.search.server.cluster.vote.VoteService;
import cc.doctor.search.server.index.manager.AllIndexService;
import cc.doctor.search.server.recovery.RecoveryService;
import cc.doctor.search.server.rpc.NettyServer;
import cc.doctor.search.server.rpc.Server;

import static cc.doctor.search.common.utils.Container.container;

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
    private AllIndexService allIndexService;
    private SchemaService schemaService;
    private Server server;
    private Scheduler scheduler;
    private NodeRole role;

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

    public SchemaService getSchemaService() {
        return schemaService;
    }

    public AllIndexService getAllIndexService() {
        return allIndexService;
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
        schemaService = new SchemaService();
        allIndexService = new AllIndexService(schemaService);
        scheduler = new Scheduler();

        container.addComponent(nodeClientHolder);
        container.addComponent(nodeService);
        container.addComponent(voteService);
        container.addComponent(routingService);
        container.addComponent(replicateService);
        container.addComponent(recoveryService);
        container.addComponent(toleranceService);
        container.addComponent(schemaService);
        container.addComponent(allIndexService);
    }

    public void start() {
        //注册监听对象，监听节点变更和主节点变更
        routingService.registerRoutingNodeListener();
        voteService.registerMasterNodeListener();
        schemaService.registerSchemaNodeListener();
        //注册节点
        nodeService.registerNode();
        //选主
        voteService.doVote();
        //生成路由表
        routingService.loadRoutingNodes();
        //加载schema
        schemaService.loadSchemas();
        //加载索引
        allIndexService.loadIndexes();
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
