package cc.doctor.wiki.search.client.rpc;

import cc.doctor.wiki.index.document.Document;
import cc.doctor.wiki.search.client.query.QueryBuilder;
import cc.doctor.wiki.search.client.rpc.result.*;

/**
 * Created by doctor on 2017/3/14.
 */
public interface Client {

    void connect(String address);

    IndexResult createIndex(String indexName);

    IndexResult dropIndex(String indexName);

    IndexResult putAlias(String indexName, String alias);

    IndexResult dropAlias(String indexName, String alias);

    SearchResult query(QueryBuilder queryBuilder);

    InsertResult insert(Document document);

    DeleteResult delete(long docId);

    BulkResult bulkInsert(Iterable<Document> documents);

    BulkResult bulkDelete(Iterable<Long> ids);

    BulkResult bulkDeleteByQuery(QueryBuilder queryBuilder);

    RpcResult sendMessage(Message message);

    RpcResult sendMessage(Message message, long timeout);
}
