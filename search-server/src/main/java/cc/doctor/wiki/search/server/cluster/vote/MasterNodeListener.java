package cc.doctor.wiki.search.server.cluster.vote;

import cc.doctor.wiki.ha.zk.ZkEventListenerAdapter;
import org.apache.zookeeper.WatchedEvent;

import java.util.concurrent.CountDownLatch;

import static cc.doctor.wiki.utils.Container.container;

/**
 * Created by doctor on 2017/3/13.
 * 主节点信息变化通知
 */
public class MasterNodeListener extends ZkEventListenerAdapter {
    public static CountDownLatch createMasterSemaphore = new CountDownLatch(1);
    private VoteService voteService;

    public MasterNodeListener() {
        voteService = container.getComponent(VoteService.class);
    }

    //when master is deleted, re-vote
    @Override
    public void onNodeDeleted(WatchedEvent watchedEvent) {
        String path = watchedEvent.getPath();
        if (path.equals(ZookeeperVote.ZK_NODE_MASTER)) {
            voteService.doVote();
        }
    }

    @Override
    public void onNodeDataChanged(WatchedEvent watchedEvent) {
        super.onNodeDataChanged(watchedEvent);
    }
}
