package cc.doctor.search.common.exceptions.rpc;

/**
 * Created by doctor on 2017/3/14.
 */
public class RpcException extends RuntimeException {
    private static final long serialVersionUID = -6366249257994021150L;

    public RpcException(String message) {
        super(message);
    }
}
