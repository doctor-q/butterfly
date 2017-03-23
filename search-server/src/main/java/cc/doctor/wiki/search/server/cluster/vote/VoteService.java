package cc.doctor.wiki.search.server.cluster.vote;

import cc.doctor.wiki.search.server.cluster.node.Node;
import cc.doctor.wiki.search.server.cluster.node.NodeService;

/**
 * Created by doctor on 2017/3/13.
 */
public class VoteService {
    private Vote vote;
    private Node node;
    private NodeService nodeService;

    public VoteService() {
        vote = new ZookeeperVote(node.getRoutingNode());
    }

    public void doVote() {
        Vote.VoteInfo voteInfo = vote.voteMaster();
        if (voteInfo.getVoteId().equals(vote.getVoteInfo().getVoteId())) {
            node.getRoutingNode().setMaster(true);
        }
        nodeService.registerNode(); //update node info
    }

    public void abdicate() {
        vote.abdicate();
    }
}
