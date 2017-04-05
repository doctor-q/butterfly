package cc.doctor.wiki.search.server.cluster.vote;

import cc.doctor.wiki.search.server.cluster.node.Node;
import cc.doctor.wiki.search.server.cluster.node.NodeService;

import java.util.Date;

/**
 * Created by doctor on 2017/3/13.
 */
public class VoteService {
    private Vote vote;
    private Node node;
    private NodeService nodeService;

    public Node getNode() {
        return node;
    }

    public VoteService(Node node) {
        this.node = node;
        this.nodeService = node.getNodeService();
        vote = new ZookeeperVote(this);
        Vote.VoteInfo voteInfo = new Vote.VoteInfo();
        voteInfo.setHost(node.getServer().getHost());
        voteInfo.setPort(node.getServer().getPort());
        voteInfo.setTimestamp(new Date().getTime());
        voteInfo.setVoteVersion(0);
        vote.setVoteInfo(voteInfo);
    }

    public void registerMasterNodeListener() {
        nodeService.registerMasterNodeListener();
    }

    public void doVote() {
        vote.getVoteInfo().setVoteId(vote.getVoteId());

        Vote.VoteInfo voteInfoAfterVote = vote.voteMaster();
        if (voteInfoAfterVote.getVoteId().equals(vote.getVoteInfo().getVoteId())) {
            node.getRoutingNode().setMaster(true);
        }
        nodeService.registerNode(); //update node info
    }

    public void abdicate() {
        vote.abdicate();
    }
}
