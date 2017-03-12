package cc.doctor.wiki.ha.zk;

import cc.doctor.wiki.utils.scanner.Scanner;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by doctor on 2017/3/12.
 */
public class ZookeeperWatcher implements Watcher {
    private static final Logger log = LoggerFactory.getLogger(ZookeeperWatcher.class);
    ConcurrentHashMap<String, ZkEventListener> zkEventListeners = new ConcurrentHashMap<>();
    private String listenerScanPackage;

    public ZookeeperWatcher(String listenerScanPackage) {
        this.listenerScanPackage = listenerScanPackage;
        Scanner scanner = new Scanner(listenerScanPackage);
        scanner.doScan();
        for (Class aClass : scanner.getScanClass().values()) {
            if (ZkEventListener.class.isAssignableFrom(aClass)) {
                try {
                    registerListener((ZkEventListener) aClass.newInstance());
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
    }

    public void registerListener(ZkEventListener zkEventListener) {
        zkEventListeners.put(zkEventListener.getClass().getName(), zkEventListener);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        Event.EventType eventType = watchedEvent.getType();
        for (ZkEventListener zkEventListener : zkEventListeners.values()) {
            if (eventType.equals(Event.EventType.NodeCreated)) {
                zkEventListener.onNodeCreate(watchedEvent);
            } else if (eventType.equals(Event.EventType.NodeDeleted)) {
                zkEventListener.onNodeDeleted(watchedEvent);
            } else if (eventType.equals(Event.EventType.NodeChildrenChanged)) {
                zkEventListener.onNodeChildrenChanged(watchedEvent);
            } else if (eventType.equals(Event.EventType.NodeDataChanged)) {
                zkEventListener.onNodeDataChanged(watchedEvent);
            }
        }
    }
}
