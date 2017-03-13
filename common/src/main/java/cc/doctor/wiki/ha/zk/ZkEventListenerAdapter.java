package cc.doctor.wiki.ha.zk;

import org.apache.zookeeper.WatchedEvent;

/**
 * Created by doctor on 2017/3/12.
 */
public class ZkEventListenerAdapter implements ZkEventListener {
    @Override
    public void onNodeCreate(WatchedEvent watchedEvent) {

    }

    @Override
    public void onNodeDeleted(WatchedEvent watchedEvent) {

    }

    @Override
    public void onNodeChildrenChanged(WatchedEvent watchedEvent) {

    }

    @Override
    public void onNodeDataChanged(WatchedEvent watchedEvent) {

    }

    @Override
    public void onZkConnected() {

    }

    @Override
    public void onAuthFailed() {

    }

    @Override
    public void onExpired() {

    }

    @Override
    public void onZkDisconnected() {

    }
}
