package cc.doctor.search.client.rpc.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by doctor on 2017/3/15.
 * 创建,删除索引,索引别名
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class IndexResult extends RpcResult {
    private static final long serialVersionUID = 8672112077762678134L;
    private String indexName;
    private String alias;
}
