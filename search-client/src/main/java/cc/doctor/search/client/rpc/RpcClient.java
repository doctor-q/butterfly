package cc.doctor.search.client.rpc;

import cc.doctor.search.client.query.QueryBuilder;
import cc.doctor.search.client.route.RoutingNode;
import cc.doctor.search.client.route.RoutingService;
import cc.doctor.search.client.route.RoutingShard;
import cc.doctor.search.client.rpc.operation.Operation;
import cc.doctor.search.client.rpc.result.*;
import cc.doctor.search.common.document.Document;
import cc.doctor.search.common.entity.Tuple;
import cc.doctor.search.common.exceptions.index.IndexException;
import cc.doctor.search.common.ha.zk.ZookeeperClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by doctor on 2017/3/14.
 * netty客户端,目前单协调者,客户端与master和datanode交互
 */
public class RpcClient implements Client {
    private ZookeeperClient zk;
    private NettyClient masterClient;
    private Map<String, NettyClient> dataClients = new HashMap<>();
    private RoutingService routingService;

    public RpcClient(String zkAddress) {
        zk = new ZookeeperClient(zkAddress, 60);
    }

    public void connect(String address) {
        masterClient = new NettyClient(address);
    }

    @Override
    public IndexResult createIndex(String indexName) {
        Message message = normalMessage().operation(Operation.CREATE_INDEX).data(indexName);
        return (IndexResult) masterClient.sendMessage(message);
    }

    @Override
    public IndexResult dropIndex(String indexName) {
        Message message = normalMessage().operation(Operation.DROP_INDEX).data(indexName);
        return (IndexResult) masterClient.sendMessage(message);
    }

    @Override
    public IndexResult putAlias(String indexName, String alias) {
        Message message = normalMessage().operation(Operation.PUT_ALIAS).data(new Tuple<>(indexName, alias));
        return (IndexResult) masterClient.sendMessage(message);
    }

    @Override
    public IndexResult dropAlias(String indexName, String alias) {
        Message message = normalMessage().operation(Operation.DROP_ALIAS).data(new Tuple<>(indexName, alias));
        return (IndexResult) masterClient.sendMessage(message);
    }

    private Message normalMessage() {
        return Message.newMessage().currentTimestamp().host(masterClient.getHost());
    }

    private NettyClient getClientByDocId(String index, String docId) {
        int primaryShards = routingService.primaryShards(index);
        if (primaryShards == 0) {
            throw new IndexException("No shard found");
        }
        int shard = docId.hashCode() % primaryShards;
        return getClientByShard(index, shard);
    }

    private NettyClient getClientByShard(String index, int shard) {
        RoutingShard routingShard = routingService.primaryShard(index, shard);
        if (routingShard == null) {
            throw new IndexException(String.format("Shard %s not found", shard));
        }
        NettyClient nettyClient = dataClients.get(routingShard.getNodeName());
        if (nettyClient == null) {
            RoutingNode node = routingService.getNode(routingShard.getNodeName());
            nettyClient = new NettyClient(String.format("%s:%s", node.getHost(), node.getPort()));
            dataClients.put(node.getNodeName(), nettyClient);
        }
        return nettyClient;
    }

    @Override
    public InsertResult insert(String index, Document document) {
        document.setIdIfAbsent();
        NettyClient nettyClient = getClientByDocId(index, document.getId());
        Message message = normalMessage().operation(Operation.ADD_DOCUMENT).data(document);
        return (InsertResult) nettyClient.sendMessage(message);
    }

    @Override
    public DeleteResult delete(String index, String docId) {
        NettyClient nettyClient = getClientByDocId(index, docId);
        Message message = normalMessage().operation(Operation.DELETE_DOCUMENT).data(docId);
        return (DeleteResult) nettyClient.sendMessage(message);
    }

    @Override
    public BulkResult bulkInsert(String index, Iterable<Document> documents) {
        int primaryShards = routingService.primaryShards(index);
        if (primaryShards == 0) {
            throw new IndexException("No shard found");
        }

        Map<Integer, ArrayList<Document>> shardDocs = new HashMap<>();
        for (Document document : documents) {
            document.setIdIfAbsent();
            int shard = document.getId().hashCode() % primaryShards;
            shardDocs.putIfAbsent(shard, new ArrayList<>());
            shardDocs.get(shard).add(document);
        }

        List<BulkResult> bulkResultList = new ArrayList<>(shardDocs.size());
        for (Map.Entry<Integer, ArrayList<Document>> entry : shardDocs.entrySet()) {
            int shard = entry.getKey();
            NettyClient nettyClient = getClientByShard(index, shard);
            Message message = normalMessage().operation(Operation.BULK_INSERT).data(entry.getValue());
            BulkResult bulkResult = (BulkResult) nettyClient.sendMessage(message);
            bulkResultList.add(bulkResult);
        }

        return merge(bulkResultList);
    }

    private BulkResult merge(List<BulkResult> bulkResults) {
        BulkResult bulkResult = new BulkResult();
        bulkResult.setShards(bulkResults.size());
        for (BulkResult result : bulkResults) {
            bulkResult.setInfluence(bulkResult.getInfluence() + result.getInfluence());
        }
        return bulkResult;
    }

    @Override
    public SearchResult query(QueryBuilder queryBuilder) {
        Message message = normalMessage().operation(Operation.QUERY).data(queryBuilder);
        return (SearchResult) masterClient.sendMessage(message);
    }

    @Override
    public BulkResult bulkDelete(String index, Iterable<String> ids) {
        int primaryShards = routingService.primaryShards(index);
        if (primaryShards == 0) {
            throw new IndexException("No shard found");
        }
        Map<Integer, ArrayList<String>> docIdMap = new HashMap<>();
        for (String id : ids) {
            int shard = id.hashCode() % primaryShards;
            docIdMap.putIfAbsent(shard, new ArrayList<>());
            docIdMap.get(shard).add(id);
        }
        List<BulkResult> bulkResultList = new ArrayList<>(docIdMap.size());
        for (Map.Entry<Integer, ArrayList<String>> entry : docIdMap.entrySet()) {
            int shard = entry.getKey();
            NettyClient nettyClient = getClientByShard(index, shard);
            Message message = normalMessage().operation(Operation.BULK_DELETE).data(entry.getValue());
            BulkResult bulkResult = (BulkResult) nettyClient.sendMessage(message);
            bulkResultList.add(bulkResult);
        }
        return merge(bulkResultList);
    }

    @Override
    public BulkResult bulkDeleteByQuery(QueryBuilder queryBuilder) {
        Message message = normalMessage().operation(Operation.DELETE_BY_QUERY).data(queryBuilder);
        return (BulkResult) masterClient.sendMessage(message);
    }

    @Override
    public RpcResult sendMessage(Message message) {
        return masterClient.sendMessage(message);
    }

    @Override
    public RpcResult sendMessage(Message message, long timeout) {
        return masterClient.sendMessage(message);
    }
}
