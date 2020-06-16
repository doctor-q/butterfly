package cc.doctor.search.server.recovery.operationlog;

import cc.doctor.search.common.utils.PropertyUtils;
import cc.doctor.search.store.StoreConfigs;

/**
 * Created by doctor on 2017/3/8.
 * 操作日志文件
 */
public abstract class OperationLogFile {
    public static final int operationLogSize = PropertyUtils.getProperty(StoreConfigs.OPERATION_LOG_SIZE_NAME, StoreConfigs.OPERATION_LOG_SIZE_DEFAULT);
    public static final String operationRoot = StoreConfigs.OPERATION_LOG_PATH_NAME;

    public boolean appendOperationLog(OperationLog operationLog) {
        if (operationLog.getData() == null) {
            return false;
        }
        return appendOperationLogInner(operationLog);
    }

    abstract boolean appendOperationLogInner(OperationLog operationLog);

    public abstract Iterable<OperationLog> loadOperationLogs(long position);
}
