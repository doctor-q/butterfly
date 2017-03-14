package cc.doctor.wiki.search.client.rpc;

import cc.doctor.wiki.index.document.Document;
import cc.doctor.wiki.search.client.query.QueryBuilder;
import cc.doctor.wiki.search.client.rpc.result.BulkResult;
import cc.doctor.wiki.search.client.rpc.result.DeleteResult;
import cc.doctor.wiki.search.client.rpc.result.InsertResult;
import cc.doctor.wiki.search.client.rpc.result.SearchResult;

/**
 * Created by doctor on 2017/3/14.
 */
public interface Client {
    SearchResult query(QueryBuilder queryBuilder);

    InsertResult insert(Document document);

    DeleteResult delete(long docId);

    BulkResult bulkInsert(Iterable<Document> documents);

    BulkResult bulkDelete(Iterable<Long> ids);

    BulkResult bulkDeleteByQuery(QueryBuilder queryBuilder);
}
