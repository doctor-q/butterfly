package cc.doctor.wiki.search.client.rpc;

import cc.doctor.wiki.common.Tuple;
import cc.doctor.wiki.search.client.query.document.Document;
import cc.doctor.wiki.search.client.rpc.operation.Operation;
import cc.doctor.wiki.search.client.query.QueryBuilder;
import cc.doctor.wiki.search.client.rpc.result.*;

import java.util.LinkedList;

/**
 * Created by doctor on 2017/3/14.
 * netty客户端,目前单协调者,客户端直接与master打交道,将请求提交给master,master做路由
 * 负责分配转发请求
 */
public class RpcClient implements Client {
    private NettyClient nettyClient;

    public RpcClient(String address) {
        nettyClient = new NettyClient(address);
    }

    public void connect(String address) {
        nettyClient.release();
        nettyClient = new NettyClient(address);
    }

    @Override
    public IndexResult createIndex(String indexName) {
        Message message = normalMessage().operation(Operation.CREATE_INDEX).data(indexName);
        return (IndexResult) nettyClient.sendMessage(message);
    }

    @Override
    public IndexResult dropIndex(String indexName) {
        Message message = normalMessage().operation(Operation.DROP_INDEX).data(indexName);
        return (IndexResult) nettyClient.sendMessage(message);
    }

    @Override
    public IndexResult putAlias(String indexName, String alias) {
        Message message = normalMessage().operation(Operation.PUT_ALIAS).data(new Tuple<>(indexName, alias));
        return (IndexResult) nettyClient.sendMessage(message);
    }

    @Override
    public IndexResult dropAlias(String indexName, String alias) {
        Message message = normalMessage().operation(Operation.DROP_ALIAS).data(new Tuple<>(indexName, alias));
        return (IndexResult) nettyClient.sendMessage(message);
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
        Message message = normalMessage().operation(Operation.ADD_DOCUMENT).data(document);
        return (InsertResult) nettyClient.sendMessage(message);
    }

    @Override
    public DeleteResult delete(long docId) {
        Message message = normalMessage().operation(Operation.DELETE_DOCUMENT).data(docId);
        return (DeleteResult) nettyClient.sendMessage(message);
    }

    @Override
    public BulkResult bulkInsert(Iterable<Document> documents) {
        LinkedList<Document> docList = new LinkedList<>();
        for (Document document : documents) {
            docList.add(document);
        }
        Message message = normalMessage().operation(Operation.BULK_INSERT).data(docList);
        return (BulkResult) nettyClient.sendMessage(message);
    }

    @Override
    public BulkResult bulkDelete(Iterable<Long> ids) {
        LinkedList<Long> docIdList = new LinkedList<>();
        for (Long id : ids) {
            docIdList.add(id);
        }
        Message message = normalMessage().operation(Operation.BULK_DELETE).data(docIdList);
        return (BulkResult) nettyClient.sendMessage(message);
    }

    @Override
    public BulkResult bulkDeleteByQuery(QueryBuilder queryBuilder) {
        Message message = normalMessage().operation(Operation.DELETE_BY_QUERY).data(queryBuilder);
        return (BulkResult) nettyClient.sendMessage(message);
    }

    @Override
    public RpcResult sendMessage(Message message) {
        return nettyClient.sendMessage(message);
    }

    @Override
    public RpcResult sendMessage(Message message, long timeout) {
        return nettyClient.sendMessage(message);
    }
}
