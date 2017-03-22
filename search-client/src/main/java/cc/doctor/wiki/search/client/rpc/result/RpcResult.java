package cc.doctor.wiki.search.client.rpc.result;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by doctor on 2017/3/14.
 */
public class RpcResult implements Serializable {
    private static final long serialVersionUID = -3441999873449002227L;
    private long timestamp;
    private long took;
    private boolean success;
    private String errorMsg;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTook() {
        return took;
    }

    public void setTook(long took) {
        this.took = took;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public static RpcResult successRpcResult() {
        RpcResult rpcResult = new RpcResult();
        rpcResult.setSuccess(true);
        rpcResult.setTimestamp(new Date().getTime());
        return rpcResult;
    }
}
