package cc.doctor.wiki.search.server.common.config;

import cc.doctor.wiki.utils.NetworkUtils;
import cc.doctor.wiki.utils.PropertyUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by doctor on 2017/3/20.
 */
public class Settings {
    public static final Settings settings = new Settings();

    private Map<String, Object> properties = new ConcurrentHashMap<>();

    private Settings() {
        add(GlobalConfig.ZOOKEEPER_CONN_STRING, GlobalConfig.ZOOKEEPER_CONN_STRING_DEFAULT);
        add(GlobalConfig.ZOOKEEPER_MASTER_PATH, GlobalConfig.ZOOKEEPER_MASTER_PATH_DEFAULT);
        add(GlobalConfig.ZOOKEEPER_NODE_PATH, GlobalConfig.ZOOKEEPER_NODE_PATH_DEFAULT);
        add(GlobalConfig.DATA_PATH, PropertyUtils.getProperty(GlobalConfig.DATA_PATH, GlobalConfig.DATA_PATH_DEFAULT));
        add(GlobalConfig.NODE_NAME, PropertyUtils.getProperty(GlobalConfig.NODE_NAME, ""));
        add(GlobalConfig.NETTY_SERVER_HOST, NetworkUtils.getOneUnLoopHost().getHostAddress());
        add(GlobalConfig.NETTY_SERVER_PORT, GlobalConfig.NETTY_SERVER_PORT_DEFAULT);
    }

    public Object get(String key) {
        return properties.get(key);
    }

    public Settings add(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    public int getInt(String key) {
        return (int) get(key);
    }

    public String getString(String key) {
        return (String) get(key);
    }
}
