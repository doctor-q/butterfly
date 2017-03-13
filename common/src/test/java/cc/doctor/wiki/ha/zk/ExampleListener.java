package cc.doctor.wiki.ha.zk;

import org.apache.zookeeper.WatchedEvent;

/**
 * Created by doctor on 2017/3/13.
 */
public class ExampleListener extends ZkEventListenerAdapter {
    @Override
    public void onNodeCreate(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }

    @Override
    public void onNodeDeleted(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }

    @Override
    public void onNodeChildrenChanged(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }

    @Override
    public void onNodeDataChanged(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }

    @Override
    public void onZkConnected() {
        System.out.println("connected.");
    }

    @Override
    public void onZkDisconnected() {
        System.out.println("disconnected zk.");
    }
}
