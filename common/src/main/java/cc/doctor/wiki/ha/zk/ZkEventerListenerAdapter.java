package cc.doctor.wiki.ha.zk;

import org.apache.zookeeper.WatchedEvent;

/**
 * Created by doctor on 2017/3/12.
 */
public class ZkEventerListenerAdapter implements ZkEventListener {
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
}
