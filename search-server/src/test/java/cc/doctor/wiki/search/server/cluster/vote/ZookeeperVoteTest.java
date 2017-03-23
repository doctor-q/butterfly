package cc.doctor.wiki.search.server.cluster.vote;

import cc.doctor.wiki.search.server.cluster.routing.RoutingNode;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by doctor on 2017/3/13.
 */
public class ZookeeperVoteTest {
    private ZookeeperVote zookeeperVote;

    @Before
    public void setUp() {
        RoutingNode routingNode = new RoutingNode();
        routingNode.setNodeId("sfafsasadsfdfs");
        zookeeperVote = new ZookeeperVote(routingNode);
        Vote.VoteInfo voteInfo = zookeeperVote.newVoteInfo();
        voteInfo.setVoteVersion(1);
        zookeeperVote.setVoteInfo(voteInfo);
    }

    @Test
    public void voteMaster() throws Exception {
        Vote.VoteInfo voteInfo = zookeeperVote.voteMaster();
        System.out.println(voteInfo);
    }

    @Test
    public void abdicate() throws Exception {

    }

}