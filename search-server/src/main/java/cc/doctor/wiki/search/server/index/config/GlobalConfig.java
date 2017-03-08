package cc.doctor.wiki.search.server.index.config;

/**
 * Created by doctor on 2017/3/8.
 */
public class GlobalConfig {
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

}
