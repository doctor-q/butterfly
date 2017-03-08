package cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog;

/**
 * Created by doctor on 2017/3/8.
 * 操作日志文件
 */
public abstract class OperationLogFile {
    public boolean appendOperationLog(OperationLog operationLog) {
        if (operationLog.getSize() == 0 || operationLog.getData() == null || operationLog.getData().length <= 0) {
            return false;
        }
        return appendOperationLogInner(operationLog);
    }

    abstract boolean appendOperationLogInner(OperationLog operationLog);
}
