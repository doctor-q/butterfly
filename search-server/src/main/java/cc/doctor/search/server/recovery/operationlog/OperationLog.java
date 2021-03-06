package cc.doctor.search.server.recovery.operationlog;

import cc.doctor.search.client.rpc.operation.Operation;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by doctor on 2017/3/8.
 * 操作日志,用于恢复数据
 */
public class OperationLog<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = -4036212834718562926L;
    private Operation operation;
    private long timestamp;
    private T data;

    public OperationLog() {
    }

    public OperationLog(Operation operation, T data) {
        this.operation = operation;
        this.timestamp = new Date().getTime();
        this.data = data;
    }

    public OperationLog(Operation operation, long timestamp, T data) {
        this.operation = operation;
        this.timestamp = timestamp;
        this.data = data;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "OperationLog{" +
                "operation=" + operation +
                ", timestamp=" + timestamp +
                '}';
    }

    public static OperationLog newOperationLog() {
        return new OperationLog();
    }
}
