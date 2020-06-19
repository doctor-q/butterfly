package cc.doctor.search.client.zookeeper;

import cc.doctor.search.common.ha.zk.ZkEventListenerAdapter;
import cc.doctor.search.common.ha.zk.ZookeeperClient;
import cc.doctor.search.client.rpc.Client;
import cc.doctor.search.common.ha.zk.ZookeeperPaths;
import org.apache.zookeeper.WatchedEvent;

/**
 * Created by doctor on 2017/3/15.
 * 监控主节点信息变化
 */
public class MasterListener extends ZkEventListenerAdapter {
    private Client client;
    private ZookeeperClient zkClient;

    public MasterListener(Client client, ZookeeperClient zkClient) {
        listenPaths.add(ZookeeperPaths.NODE_MASTER);
        this.client = client;
        this.zkClient = zkClient;
    }

    @Override
    public void onNodeCreate(WatchedEvent watchedEvent) {
        reConnectMaster();
    }

    @Override
    public void onNodeDataChanged(WatchedEvent watchedEvent) {
        reConnectMaster();
    }

    private void reConnectMaster() {
        boolean masterExist = zkClient.existsNode(ZookeeperPaths.NODE_MASTER);
        if (masterExist) {
            String masterInfo = zkClient.readData(ZookeeperPaths.NODE_MASTER);
            client.connect(getMasterAddress(masterInfo));
        }
    }

    // masterInfo: host=127.0.0.1&port=7070
    private String getMasterAddress(String masterInfo) {
        if (masterInfo == null) {
            return null;
        }
        String[] split = masterInfo.split("&");
        String host = null, port = null;
        for (String nameValue : split) {
            String[] nameValuePair = nameValue.split("=");
            if (nameValuePair.length == 2) {
                if (nameValuePair[0].equals("host")) {
                    host = nameValuePair[1];
                } else if (nameValuePair[0].equals("port")) {
                    port = nameValuePair[1];
                }
            }
        }
        if (host != null && port != null) {
            return host + ":" + port;
        }
        return null;
    }
}
