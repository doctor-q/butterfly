package cc.doctor.wiki.ha.zk;

import org.apache.zookeeper.WatchedEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by doctor on 2017/3/12.
 */
public class ZkEventListenerAdapter implements ZkEventListener {
    protected List<String> listenPaths = new LinkedList<>();
    @Override
    public List<String> listenPaths() {
        return listenPaths;
    }

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
