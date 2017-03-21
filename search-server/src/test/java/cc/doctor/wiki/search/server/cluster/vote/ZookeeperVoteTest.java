package cc.doctor.wiki.search.server.cluster.vote;

import cc.doctor.wiki.ha.zk.ZookeeperClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static cc.doctor.wiki.search.server.common.config.Settings.settings;

/**
 * Created by doctor on 2017/3/13.
 */
public class ZookeeperVoteTest {
    private ZookeeperVote zookeeperVote;
    ZookeeperClient client = ZookeeperClient.getClient((String) settings.get(""));

    @Before
    public void setUp() {
        zookeeperVote = new ZookeeperVote(client);
        Vote.VoteInfo voteInfo = new Vote.VoteInfo();
        voteInfo.setVoteId(1L);
        voteInfo.setHost("localhost");
        voteInfo.setPort("1234");
        voteInfo.setTimestamp(new Date().getTime());
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

    @After
    public void release() {
        client.releaseConnection();
    }

}