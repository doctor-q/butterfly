package cc.doctor.search.client.rpc.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by doctor on 2017/3/14.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShardRpcResult extends RpcResult {
    private static final long serialVersionUID = -4554325123779275530L;
    private int shards;
    private int success;
    private int failed;
}
