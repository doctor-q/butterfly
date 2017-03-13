package cc.doctor.wiki.search.server.cluster.vote;

import cc.doctor.wiki.search.server.cluster.node.LifeCycle;
import cc.doctor.wiki.search.server.cluster.node.Node;

/**
 * Created by doctor on 2017/3/13.
 */
public class VoteService implements LifeCycle{
    private Vote vote;
    private Node node;

    public VoteService() {
        vote = new ZookeeperVote(null);
    }

    @Override
    public void onNodeStart() {

    }

    @Override
    public void onNodeStarted() {
        Vote.VoteInfo voteInfo = vote.voteMaster();
        if (voteInfo.getVoteId() == vote.getVoteInfo().getVoteId()) {
            node.getRoutingNode().setMaster(true);
        }
    }

    @Override
    public void onNodeStop() {
        vote.abdicate();
    }
}
