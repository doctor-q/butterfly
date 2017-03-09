package cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog;

import cc.doctor.wiki.operation.Operation;

import java.io.Serializable;

/**
 * Created by doctor on 2017/3/8.
 * 操作日志,用于恢复数据
 */
public class OperationLog implements Serializable {
    private static final long serialVersionUID = -4036212834718562926L;
    private int size;
    private Operation operation;
    private long timestamp;
    private byte[] data;

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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}