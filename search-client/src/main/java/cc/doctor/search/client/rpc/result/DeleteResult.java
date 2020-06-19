package cc.doctor.search.client.rpc.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by doctor on 2017/3/14.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteResult extends ShardRpcResult {
    private static final long serialVersionUID = -5128800707535329925L;

    private boolean found;
    private long docId;
    private long version;   //删除后版本会变化
    private String index;
}
