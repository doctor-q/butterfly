package cc.doctor.search.client.route;

import lombok.Data;

/**
 * Created by doctor on 2017/3/13.
 * 分片信息
 */
@Data
public class RoutingShard {
    private String nodeName;
    private String indexName;
    private String alias;
    private int shardId;
    private boolean primary;

    public RoutingShard(String indexName, String nodeName, int shardId) {
        this.indexName = indexName;
        this.nodeName = nodeName;
        this.shardId = shardId;
    }
}
