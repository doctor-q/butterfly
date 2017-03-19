package cc.doctor.wiki.search.server.cluster.routing;

/**
 * Created by doctor on 2017/3/13.
 * 分片信息
 */
public class RoutingShard {
    private String indexName;
    private String alias;
    private String nodeId;
    private int shardId;

    public RoutingShard(String indexName, String nodeId, int shardId) {
        this.indexName = indexName;
        this.nodeId = nodeId;
        this.shardId = shardId;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public int getShardId() {
        return shardId;
    }

    public void setShardId(int shardId) {
        this.shardId = shardId;
    }
}
