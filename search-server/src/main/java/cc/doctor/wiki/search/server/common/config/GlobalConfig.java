package cc.doctor.wiki.search.server.common.config;

/**
 * Created by doctor on 2017/3/8.
 */
public class GlobalConfig {
    public static final String DEFAULT_SHARDS_NUM = "shards.num.default";
    public static final int DEFAULT_SHARDS_NUM_DEFAULT = 5;
    //单个Source文件大小
    public static final String SOURCE_FILE_SIZE_NAME = "file.size.source";
    public static final int SOURCE_FILE_SIZE_DEFUALT = 100 * 1024 * 1024;   //默认100M
    public static final int SOURCE_FILE_SIZE_MAX = 800 * 1024 * 1024;   //最大800M
    public static final int SOURCE_FILE_SIZE_MIN = 10 * 1024 * 1024;   //最大10M

    //单个倒排文件大小
    public static final String INVERTED_FILE_SIZE_NAME = "file.size.source";
    public static final int INVERTED_FILE_SIZE_DEFUALT = 10 * 1024 * 1024;   //默认10M
    public static final int INVERTED_FILE_SIZE_MAX = 100 * 1024 * 1024;   //最大100M
    public static final int INVERTED_FILE_SIZE_MIN = 1024 * 1024;   //最大1M

    //单个操作日志文件大小
    public static final String OPERATION_LOG_SIZE_NAME = "file.size.operation.log";
    public static final int OPERATION_LOG_SIZE_DEFAULT = 100 * 1024 * 1024;   //默认100M
    public static final int OPERATION_LOG_SIZE_MAX = 500 * 1024 * 1024;   //最大100M
    public static final int OPERATION_LOG_SIZE_MIN = 10 * 1024 * 1024;   //最小10M
    //是否保留查询日志
    public static final String OPERATION_LOG_QUERY = "operation.log.query";
    public static final Boolean OPERATION_LOG_QUERY_DEFAULT = false;
    //索引文档线程数
    public static final String THREAD_NUM_WRITE_DOCUMENT = "thread.num.write.document";
    public static final int THREAD_NUM_WRITE_DOCUMENT_DEFAULT = 10;
    public static final int THREAD_NUM_WRITE_DOCUMENT_MAX = 20;
    public static final int THREAD_NUM_WRITE_DOCUMENT_MIN = 1;
    //路径
    public static final String DATA_PATH = "data.path";
    public static final String DATA_PATH_DEFAULT = "/tmp/es/data";
    public static final String OPERATION_LOG_PATH_NAME = "operationlog";
    public static final String SOURCE_PATH_NAME = "source";
    public static final String INDEX_PATH_NAME = "index";
    public static final String CHECKPOINT_FILE_NAME = "checkpoint";
    public static final String LOG_PATH = "logs";
    //rpc
    public static final String NETTY_SERVER_PORT = "netty.server.port";
    public static final int NETTY_SERVER_PORT_DEFAULT = 1218;

}