package cc.doctor.wiki.search.client.rpc.result;

import java.io.Serializable;

/**
 * Created by doctor on 2017/3/14.
 */
public class RpcResult implements Serializable {
    private static final long serialVersionUID = -3441999873449002227L;
    private long took;
    private boolean success;
    private String errorMsg;
}
