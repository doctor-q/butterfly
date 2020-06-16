package cc.doctor.search.store;

public class StoreConfigs {
    //单个操作日志文件大小
    public static final String OPERATION_LOG_SIZE_NAME = "file.size.operation.log";
    public static final int OPERATION_LOG_SIZE_DEFAULT = 100 * 1024 * 1024;   //默认100M
    public static final int OPERATION_LOG_SIZE_MAX = 500 * 1024 * 1024;   //最大100M
    public static final int OPERATION_LOG_SIZE_MIN = 10 * 1024 * 1024;   //最小10M

    //路径
    public static final String DATA_PATH = "data.path";
    public static final String DATA_PATH_DEFAULT = "/tmp/es/data";
    public static final String OPERATION_LOG_PATH_NAME = "operationlog";
    public static final String SOURCE_PATH_NAME = "source";
    public static final String INDEX_PATH_NAME = "index";
    public static final String CHECKPOINT_FILE_NAME = "checkpoint";
    public static final String LOG_PATH = "logs";

    //单个倒排文件大小
    public static final String INVERTED_FILE_SIZE_NAME = "file.size.inverted";
    public static final int INVERTED_FILE_SIZE_DEFAULT = 10 * 1024 * 1024;   //默认10M
    public static final int INVERTED_FILE_SIZE_MAX = 100 * 1024 * 1024;   //最大100M
    public static final int INVERTED_FILE_SIZE_MIN = 1024 * 1024;   //最大1M
    public static final String INVERTED_FILE_PATH_NAME = "inverted";
    public static final String FLUSH_INVERTED_TABLE_NUM = "flush.inverted.table.num";
    public static final int FLUSH_INVERTED_TABLE_NUM_DEFAULT = 1000;
    public static final String CACHE_INVERTED_TABLE_SIZE = "cache.inverted.table.size";
    public static final int CACHE_INVERTED_TABLE_SIZE_DEFAULT = 1000;

    //词典
    public static final String DICT_FILE_NAME = "dict";
    public static final String DICT_FILE_SIZE = "dict.file.size"; //100M
    public static final int DICT_FILE_SIZE_DEFAULT = 1024 * 1024 * 100; //100M
    public static final String DICT_INDEX_FILE_NAME = "dict.idx";
    public static final String DICT_INDEX_FILE_SIZE = "dict.index.file.size"; //100M
    public static final int DICT_INDEX_FILE_SIZE_DEFAULT = 10 * 1024 * 1024; //10M

    //单个Source文件大小
    public static final String SOURCE_FILE_SIZE_NAME = "file.size.source";
    public static final int SOURCE_FILE_SIZE_DEFUALT = 10 * 1024 * 1024;   //默认10M
    public static final int SOURCE_FILE_SIZE_MAX = 100 * 1024 * 1024;   //最大800M
    public static final int SOURCE_FILE_SIZE_MIN = 1024 * 1024;   //最小1M
}
