package cc.doctor.search.server.index.store.indices.recovery.operationlog;

import cc.doctor.search.server.common.config.GlobalConfig;
import cc.doctor.search.server.index.manager.IndexManagerInner;
import cc.doctor.search.common.utils.PropertyUtils;

/**
 * Created by doctor on 2017/3/8.
 * 操作日志文件
 */
public abstract class OperationLogFile {
    public static final int operationLogSize = PropertyUtils.getProperty(GlobalConfig.OPERATION_LOG_SIZE_NAME, GlobalConfig.OPERATION_LOG_SIZE_DEFAULT);
    public static final String operationRoot = IndexManagerInner.dataRoot + "/" + GlobalConfig.OPERATION_LOG_PATH_NAME;

    public boolean appendOperationLog(OperationLog operationLog) {
        if (operationLog.getData() == null) {
            return false;
        }
        return appendOperationLogInner(operationLog);
    }

    abstract boolean appendOperationLogInner(OperationLog operationLog);

    public abstract Iterable<OperationLog> loadOperationLogs(long position);
}
