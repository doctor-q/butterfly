package cc.doctor.search.common.ha.zk;

import cc.doctor.search.common.utils.PropertyUtils;
import cc.doctor.search.common.utils.scanner.Scanner;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by doctor on 2017/3/12.
 */
public class ZookeeperWatcher implements Watcher {
    private static final Logger log = LoggerFactory.getLogger(ZookeeperWatcher.class);
    private String listenerScanPackage = PropertyUtils.getProperty("zk.listener.scan.package", "cc.doctor.wiki.ha.zk.listener");
    private ZookeeperClient zookeeperClient;

    public ZookeeperWatcher(ZookeeperClient zookeeperClient) {
        this.zookeeperClient = zookeeperClient;
    }

    public void scanListeners() {
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

    public <T extends ZkEventListener> void registerListener(Class<T> zkEventListenerClass) {
        try {
            ZkEventListener zkEventListener = zkEventListenerClass.newInstance();
            registerListener(zkEventListener);
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("", e);
        }
    }

    public void registerListener(ZkEventListener zkEventListener) {
        for (String path : zkEventListener.listenPaths()) {
            //monitor all
            zookeeperClient.existsNode(path);
            zookeeperClient.readData(path);
            zookeeperClient.getChildren(path);
            ZkListenerDispatcher.zkListenerDispatcher.registerListener(path, zkEventListener);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        // 连接状态
        Event.KeeperState keeperState = event.getState();
        // 事件类型
        Event.EventType eventType = event.getType();

        List<ZkEventListener> zkEventListeners = ZkListenerDispatcher.zkListenerDispatcher.getListeners(event.getPath());
        for (ZkEventListener zkEventListener : zkEventListeners) {
            if (Event.KeeperState.SyncConnected == keeperState) {
                // 成功连接上ZK服务器
                if (Event.EventType.None == eventType) {
                    zkEventListener.onZkConnected();
                } else if (Event.EventType.NodeCreated == eventType) {
                    zkEventListener.onNodeCreate(event);
                } else if (Event.EventType.NodeDataChanged == eventType) {
                    zkEventListener.onNodeDataChanged(event);
                } else if (Event.EventType.NodeChildrenChanged == eventType) {
                    zkEventListener.onNodeChildrenChanged(event);
                } else if (Event.EventType.NodeDeleted == eventType) {
                    zkEventListener.onNodeDeleted(event);
                }

            } else if (Event.KeeperState.Disconnected == keeperState) {
                zkEventListener.onZkDisconnected();
            } else if (Event.KeeperState.AuthFailed == keeperState) {
                zkEventListener.onAuthFailed();
            } else if (Event.KeeperState.Expired == keeperState) {
                zkEventListener.onExpired();
            }

        }
    }
}
