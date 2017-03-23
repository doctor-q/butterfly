package cc.doctor.wiki.search.client.zookeeper;

import cc.doctor.wiki.ha.zk.ZkEventListenerAdapter;
import cc.doctor.wiki.ha.zk.ZookeeperClient;
import cc.doctor.wiki.search.client.rpc.Client;
import org.apache.zookeeper.WatchedEvent;

/**
 * Created by doctor on 2017/3/15.
 * 监控主节点信息变化
 */
public class MasterListener extends ZkEventListenerAdapter {
    public static final String ZK_NODE_MASTER = "/es/metadata/master";
    private Client client;
    private ZookeeperClient zkClient;

    public MasterListener(Client client, ZookeeperClient zkClient) {
        listenPaths.add(ZK_NODE_MASTER);
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
        boolean masterExist = zkClient.existsNode(ZK_NODE_MASTER);
        if (masterExist) {
            String masterInfo = zkClient.readData(ZK_NODE_MASTER);
            client.connect(getMasterAddress(masterInfo));
        }
    }

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
