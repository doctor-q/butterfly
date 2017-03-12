package cc.doctor.wiki.ha.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by doctor on 2017/3/12.
 */
public class ZookeeperClient {
    private Logger log = LoggerFactory.getLogger(ZookeeperClient.class);
    private static final int SESSION_TIMEOUT = 10000;
    private static final String CONNECTION_STRING = "127.0.0.1:2181";
    private static final String ZK_PATH = "/es";
    private ZooKeeper zk = null;
    private ZookeeperWatcher zookeeperWatcher;

    private CountDownLatch connectedSemaphore = new CountDownLatch(1);

    /**
     * 创建ZK连接
     *
     * @param connectString  ZK服务器地址列表
     * @param sessionTimeout Session超时时间
     */
    public void createConnection(String connectString, int sessionTimeout) {
        this.releaseConnection();
        try {
            zk = new ZooKeeper(connectString, sessionTimeout, zookeeperWatcher);
            connectedSemaphore.await();
        } catch (InterruptedException e) {
            log.error("连接创建失败，发生 InterruptedException");
        } catch (IOException e) {
            log.error("连接创建失败，发生 IOException");
        }
    }

    /**
     * 关闭ZK连接
     */
    public void releaseConnection() {
        if (zk != null) {
            try {
                this.zk.close();
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * 创建节点
     *
     * @param path 节点path
     * @param data 初始数据内容
     * @return
     */
    public boolean createPath(String path, String data) {
        try {
            this.zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (KeeperException e) {
            log.error("节点创建失败，发生KeeperException");
        } catch (InterruptedException e) {
            log.error("节点创建失败，发生 InterruptedException");
        }
        return true;
    }

    /**
     * 读取指定节点数据内容
     *
     * @param path 节点path
     * @return
     */
    public String readData(String path) {
        try {
            return new String(this.zk.getData(path, false, null));
        } catch (KeeperException e) {
            log.error("读取数据失败，发生KeeperException，path: {}", path);
            return null;
        } catch (InterruptedException e) {
            log.error("读取数据失败，发生 InterruptedException，path: {}", path);
            return null;
        }
    }

    /**
     * 更新指定节点数据内容
     *
     * @param path 节点path
     * @param data 数据内容
     * @return
     */
    public boolean writeData(String path, String data) {
        try {
            this.zk.setData(path, data.getBytes(), -1);
        } catch (KeeperException e) {
            log.error("更新数据失败，发生KeeperException，path: {}", path);
        } catch (InterruptedException e) {
            log.error("更新数据失败，发生 InterruptedException，path: {}", path);
        }
        return false;
    }

    /**
     * 删除指定节点
     *
     * @param path 节点path
     */
    public void deleteNode(String path) {
        try {
            this.zk.delete(path, -1);
        } catch (KeeperException e) {
            log.error("删除节点失败，发生KeeperException，path: {}", path);
        } catch (InterruptedException e) {
            log.error("删除节点失败，发生 InterruptedException，path: {}", path);
        }
    }
}
