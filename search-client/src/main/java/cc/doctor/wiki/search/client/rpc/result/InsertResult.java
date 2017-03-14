package cc.doctor.wiki.search.client.rpc.result;

/**
 * Created by doctor on 2017/3/14.
 */
public class InsertResult extends ShardRpcResult {
    private static final long serialVersionUID = 7436772143600321983L;

    private long docId;
    private long version;
    private String index;
}
