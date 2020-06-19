package cc.doctor.search.client.rpc.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by doctor on 2017/3/14.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InsertResult extends ShardRpcResult {
    private static final long serialVersionUID = 7436772143600321983L;

    private long docId;
    private long version;
    private String index;
}
