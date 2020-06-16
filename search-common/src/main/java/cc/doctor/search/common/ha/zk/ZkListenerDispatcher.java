package cc.doctor.search.common.ha.zk;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by doctor on 2017/3/23.
 */
public class ZkListenerDispatcher {
    public static final ZkListenerDispatcher zkListenerDispatcher = new ZkListenerDispatcher();
    private Map<String, List<ZkEventListener>> pathZkListeners = new HashMap<>();

    public void registerListener(String path, ZkEventListener zkEventListener) {
        if (path == null || zkEventListener == null) {
            return;
        }
        List<ZkEventListener> zkEventListeners = pathZkListeners.get(path);
        if (zkEventListeners == null) {
            zkEventListeners = new LinkedList<>();
            pathZkListeners.put(path, zkEventListeners);
        }
        zkEventListeners.add(zkEventListener);
    }

    public List<ZkEventListener> getListeners(String path) {
        if (path == null) {
            return new LinkedList<>();
        }
        List<ZkEventListener> zkEventListeners = pathZkListeners.get(path);
        if (zkEventListeners == null) {
            return new LinkedList<>();
        }
        return zkEventListeners;
    }
}
