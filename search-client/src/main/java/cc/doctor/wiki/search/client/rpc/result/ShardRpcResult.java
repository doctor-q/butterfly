package cc.doctor.wiki.search.client.rpc.result;

/**
 * Created by doctor on 2017/3/14.
 */
public class ShardRpcResult extends RpcResult {
    private static final long serialVersionUID = -4554325123779275530L;
    private int shards;
    private int success;
    private int failed;
}
