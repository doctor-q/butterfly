package cc.doctor.wiki.ha.zk;

import org.apache.zookeeper.WatchedEvent;

/**
 * Created by doctor on 2017/3/12.
 */
public interface ZkEventListener {
    void onNodeCreate(WatchedEvent watchedEvent);

    void onNodeDeleted(WatchedEvent watchedEvent);

    void onNodeChildrenChanged(WatchedEvent watchedEvent);

    void onNodeDataChanged(WatchedEvent watchedEvent);

    void onZkConnected();
    void onAuthFailed();
    void onExpired();
    void onZkDisconnected();
}
