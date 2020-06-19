package cc.doctor.search.client.rpc.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by doctor on 2017/3/14.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BulkResult extends ShardRpcResult {
    private static final long serialVersionUID = 3900142062218445486L;

    private int influence;
}
