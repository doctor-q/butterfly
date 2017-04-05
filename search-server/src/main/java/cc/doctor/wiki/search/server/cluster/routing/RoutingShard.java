package cc.doctor.wiki.search.server.cluster.routing;

/**
 * Created by doctor on 2017/3/13.
 * 分片信息
 */
public class RoutingShard {
    private String indexName;
    private String alias;
    private String nodeName;
    private int shardId;

    public RoutingShard(String indexName, String nodeName, int shardId) {
        this.indexName = indexName;
        this.nodeName = nodeName;
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

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public int getShardId() {
        return shardId;
    }

    public void setShardId(int shardId) {
        this.shardId = shardId;
    }
}
