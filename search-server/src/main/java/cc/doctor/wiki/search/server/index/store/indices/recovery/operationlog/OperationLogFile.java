package cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog;

import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.utils.PropertyUtils;
import cc.doctor.wiki.search.server.index.store.indices.recovery.RecoveryService;

/**
 * Created by doctor on 2017/3/8.
 * 操作日志文件
 */
public abstract class OperationLogFile {
    private RecoveryService recoveryService;
    public static final int operationLogSize = PropertyUtils.getProperty(GlobalConfig.OPERATION_LOG_SIZE_NAME, GlobalConfig.OPERATION_LOG_SIZE_DEFAULT);

    public OperationLogFile(RecoveryService recoveryService) {
        this.recoveryService = recoveryService;
    }

    public boolean appendOperationLog(OperationLog operationLog) {
        if (operationLog.getSize() == 0 || operationLog.getData() == null || operationLog.getData().length <= 0) {
            return false;
        }
        return appendOperationLogInner(operationLog);
    }

    abstract boolean appendOperationLogInner(OperationLog operationLog);

    abstract Iterable<OperationLog> loadOperationLogs(int position);
}
