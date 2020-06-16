package cc.doctor.search.server.common.config;

/**
 * Created by doctor on 2017/3/8.
 */
public class GlobalConfig {
    //分片
    public static final String DEFAULT_SHARDS_NUM = "shards.num.default";
    public static final int DEFAULT_SHARDS_NUM_DEFAULT = 5;
    //副本
    public static final String DEFAULT_REPLICATION_NUM = "replication.num.default";
    public static final int DEFAULT_REPLICATION_NUM_DEFAULT = 1;

    //是否保留查询日志
    public static final String OPERATION_LOG_QUERY = "operation.log.query";
    public static final boolean OPERATION_LOG_QUERY_DEFAULT = false;
    //索引文档线程数
    public static final String THREAD_NUM_WRITE_DOCUMENT = "thread.num.write.document";
    public static final int THREAD_NUM_WRITE_DOCUMENT_DEFAULT = 10;
    public static final int THREAD_NUM_WRITE_DOCUMENT_MAX = 20;
    public static final int THREAD_NUM_WRITE_DOCUMENT_MIN = 1;

    //rpc
    public static final String NETTY_SERVER_HOST = "netty.server.host";
    public static final String NETTY_SERVER_PORT = "netty.server.port";
    public static final int NETTY_SERVER_PORT_DEFAULT = 1218;

    //zk
    public static final String ZOOKEEPER_CONN_STRING = "zookeeper.conn.string";
    public static final String ZOOKEEPER_CONN_STRING_DEFAULT = "127.0.0.1:2181";
    public static final String ZOOKEEPER_MASTER_PATH = "zk.master.path";
    public static final String ZOOKEEPER_MASTER_PATH_DEFAULT = "/es/metadata/master";
    public static final String ZOOKEEPER_NODE_PATH = "zk.metadata.node.path";
    public static final String ZOOKEEPER_NODE_PATH_DEFAULT = "/es/metadata/nodes";
    public static final String ZOOKEEPER_ROUTING_PATH = "/es/metadata/routing";

    public static final String NODE_NAME = "node.name";
    public static final String MAX_LOSS_CONNECTION_TIMES = "loss.connection.times.max";
    public static final String ZOOKEEPER_INDEX_SCHEMA_PATH = "zk.index.schema.path";
    public static final String ZOOKEEPER_INDEX_SCHEMA_PATH_DEFAULT = "/es/metadata/schema";
}
