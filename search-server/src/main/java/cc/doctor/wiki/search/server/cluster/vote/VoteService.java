package cc.doctor.wiki.search.server.cluster.vote;

import cc.doctor.wiki.ha.zk.ZookeeperClient;
import cc.doctor.wiki.search.server.cluster.node.Node;
import cc.doctor.wiki.search.server.cluster.node.NodeService;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;

import static cc.doctor.wiki.search.server.common.config.Settings.settings;

/**
 * Created by doctor on 2017/3/13.
 */
public class VoteService {
    private Vote vote;
    private Node node;
    private NodeService nodeService;

    public VoteService() {
        vote = new ZookeeperVote(ZookeeperClient.getClient(settings.get(GlobalConfig.ZOOKEEPER_CONN_STRING)));
    }

    public void doVote() {
        Vote.VoteInfo voteInfo = vote.voteMaster();
        if (voteInfo.getVoteId() == vote.getVoteInfo().getVoteId()) {
            node.getRoutingNode().setMaster(true);
        }
        nodeService.registerNode(); //update node info
    }

    public void abdicate() {
        vote.abdicate();
    }
}
