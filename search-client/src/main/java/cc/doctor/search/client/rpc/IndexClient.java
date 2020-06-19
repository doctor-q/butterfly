package cc.doctor.search.client.rpc;

import cc.doctor.search.client.rpc.result.IndexResult;

public interface IndexClient {
    /**
     * create an index
     * @param indexName index name
     */
    IndexResult createIndex(String indexName);

    /**
     * drop an index
     * @param indexName index name
     */
    IndexResult dropIndex(String indexName);

    /**
     * set index alias index
     * @param indexName index name
     */
    IndexResult putAlias(String indexName, String alias);

    /**
     * drop alias of an index
     * @param indexName index name
     */
    IndexResult dropAlias(String indexName, String alias);
}
