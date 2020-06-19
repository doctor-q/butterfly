package cc.doctor.search.client.rpc;

import cc.doctor.search.client.query.QueryBuilder;
import cc.doctor.search.client.rpc.result.*;
import cc.doctor.search.common.document.Document;

/**
 * Created by doctor on 2017/3/14.
 */
public interface Client extends IndexClient {

    /**
     * connect to master
     *
     * @param address master address
     */
    void connect(String address);

    /**
     * do query
     */
    SearchResult query(QueryBuilder queryBuilder);

    /**
     * insert an document
     */
    InsertResult insert(String index, Document document);

    /**
     * delete an document
     */
    DeleteResult delete(String index, String docId);

    /**
     * insert documents
     */
    BulkResult bulkInsert(String index, Iterable<Document> documents);

    /**
     * delete documents
     */
    BulkResult bulkDelete(String index, Iterable<String> ids);

    /**
     * delete by query
     *
     * @param queryBuilder a query for deleting documents
     */
    BulkResult bulkDeleteByQuery(QueryBuilder queryBuilder);

    RpcResult sendMessage(Message message);

    RpcResult sendMessage(Message message, long timeout);
}
