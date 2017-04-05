package cc.doctor.wiki.search.server.cluster.node;

import cc.doctor.wiki.schedule.Scheduler;
import cc.doctor.wiki.search.server.cluster.routing.RoutingNode;
import cc.doctor.wiki.search.server.cluster.routing.RoutingService;
import cc.doctor.wiki.search.server.cluster.vote.VoteService;
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
        server = new NettyServer();
        nodeService = new NodeService(this);
        voteService = new VoteService(this);
        routingService = new RoutingService();
        scheduler = new Scheduler();

        container.addComponent(nodeService);
        container.addComponent(voteService);
        container.addComponent(routingService);
    }

    public void start() {
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
