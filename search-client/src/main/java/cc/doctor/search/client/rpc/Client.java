package cc.doctor.search.client.rpc;

import cc.doctor.search.common.document.Document;
import cc.doctor.search.client.rpc.result.*;
import cc.doctor.search.client.query.QueryBuilder;

/**
 * Created by doctor on 2017/3/14.
 */
public interface Client {

    /**
     * connect to server
     * @param address server address
     */
    void connect(String address);

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

    /**
     * do query
     */
    SearchResult query(QueryBuilder queryBuilder);

    /**
     * insert an document
     */
    InsertResult insert(Document document);

    /**
     * delete an document
     */
    DeleteResult delete(long docId);

    /**
     * insert documents
     */
    BulkResult bulkInsert(Iterable<Document> documents);

    /**
     * delete documents
     */
    BulkResult bulkDelete(Iterable<Long> ids);

    /**
     * delete by query
     * @param queryBuilder a query for deleting documents
     */
    BulkResult bulkDeleteByQuery(QueryBuilder queryBuilder);

    RpcResult sendMessage(Message message);

    RpcResult sendMessage(Message message, long timeout);
}
