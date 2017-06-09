package cc.doctor.search.server.cluster.node.tolerance;

import cc.doctor.search.server.cluster.node.Node;
import cc.doctor.search.server.cluster.routing.RoutingNode;
import cc.doctor.search.server.cluster.routing.RoutingService;
import cc.doctor.search.client.rpc.Client;
import cc.doctor.search.client.rpc.Message;
import cc.doctor.search.client.rpc.operation.Operation;
import cc.doctor.search.client.rpc.result.PingResult;
import cc.doctor.search.client.rpc.result.RpcResult;
import cc.doctor.search.server.cluster.node.NodeService;
import cc.doctor.search.server.cluster.vote.VoteService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by doctor on 2017/3/22.
 */
public class ToleranceService {
    private Node node;
    private RoutingService routingService;
    private NodeService nodeService;
    private VoteService voteService;
    private Map<String, Client> nodeClients;
    private Map<String, AtomicInteger> nodeLoss = new HashMap<>();

    public ToleranceService(Node node) {
        this.node = node;
        this.routingService = node.getRoutingService();
        this.nodeService = node.getNodeService();
        this.voteService = node.getVoteService();
    }

    public void reportMasterLoss() {
        RoutingNode master = routingService.getMaster();
        for (String nodeName : nodeClients.keySet()) {
            if (!master.getNodeName().equals(nodeName)) {
                Client client = nodeClients.get(nodeName);
                Message message = Message.newMessage().operation(Operation.MASTER_LOSS).currentTimestamp();
                client.sendMessage(message);
            }
        }
    }

    public void reportSlaveLoss() {
        RoutingNode master = routingService.getMaster();
        Client client = nodeClients.get(master.getNodeName());
        Message message = Message.newMessage().operation(Operation.NODE_LOSS).currentTimestamp();
        client.sendMessage(message);
    }

    public RpcResult responsePing(Message message) {
        return new PingResult();
    }

    public RpcResult doMasterLoss(Message message) {
        String nodeName = (String) message.getData();
        RoutingNode routingNode = routingService.getNode(nodeName);
        if (routingNode.isMaster()) {
            AtomicInteger nodeLoss = this.nodeLoss.get(nodeName);
            if (nodeLoss.incrementAndGet() > routingService.getRoutingNodes().size() / 2) {
                //remove node
                voteService.doVote();
                //如果自己是master,删掉这个节点
                if (node.getRoutingNode().isMaster()) {
                    nodeService.removeNode(routingNode);
                }
                this.nodeLoss.remove(nodeName);
            }
        }
        return RpcResult.successRpcResult();
    }

    public RpcResult doNodeLoss(Message message) {
        boolean master = node.getRoutingNode().isMaster();
        if (master) {
            String nodeName = (String) message.getData();
            AtomicInteger nodeLoss = this.nodeLoss.get(nodeName);
            if (nodeLoss.incrementAndGet() > routingService.getRoutingNodes().size() / 2) {
                //remove node
                nodeService.removeNode(routingService.getNode(nodeName));
                this.nodeLoss.remove(nodeName);
            }
        }
        return RpcResult.successRpcResult();
    }
}
