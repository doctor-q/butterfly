package cc.doctor.wiki.search.client.rpc;

import cc.doctor.wiki.index.document.Document;
import cc.doctor.wiki.protocol.operation.Operation;
import cc.doctor.wiki.search.client.query.QueryBuilder;
import cc.doctor.wiki.search.client.rpc.result.*;

/**
 * Created by doctor on 2017/3/14.
 */
public class RpcClient implements Client {
    private NettyClient nettyClient;

    public RpcClient() {
        nettyClient = new NettyClient();
    }

    private Message normalMessage() {
        return Message.newMessage().currentTimestamp().host(nettyClient.getHost());
    }

    @Override
    public SearchResult query(QueryBuilder queryBuilder) {
        Message message = normalMessage().operation(Operation.QUERY).data(queryBuilder);
        return (SearchResult) nettyClient.sendMessage(message);
    }

    @Override
    public InsertResult insert(Document document) {
        return null;
    }

    @Override
    public DeleteResult delete(long docId) {
        return null;
    }

    @Override
    public BulkResult bulkInsert(Iterable<Document> documents) {
        return null;
    }

    @Override
    public BulkResult bulkDelete(Iterable<Long> ids) {
        return null;
    }

    @Override
    public BulkResult bulkDeleteByQuery(QueryBuilder queryBuilder) {
        return null;
    }

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient();
        rpcClient.query(QueryBuilder.queryBuilder());
    }
}
