package cc.doctor.search.client.rpc.result;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by doctor on 2017/3/14.
 */
@Data
public class RpcResult implements Serializable {
    private static final long serialVersionUID = -3441999873449002227L;
    private long timestamp;
    private long took;
    private boolean success;
    private String errorMsg;

    public static RpcResult successRpcResult() {
        RpcResult rpcResult = new RpcResult();
        rpcResult.setSuccess(true);
        rpcResult.setTimestamp(new Date().getTime());
        return rpcResult;
    }
}
