package cc.doctor.wiki.search.server.cluster.replicate;

import cc.doctor.wiki.common.Tuple;
import cc.doctor.wiki.index.document.Document;
import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.client.rpc.Client;
import cc.doctor.wiki.search.client.rpc.Message;
import cc.doctor.wiki.search.client.rpc.request.*;
import cc.doctor.wiki.search.client.rpc.result.IndexResult;
import cc.doctor.wiki.search.client.rpc.result.RpcResult;
import cc.doctor.wiki.search.server.cluster.routing.RoutingService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cc.doctor.wiki.search.server.index.manager.IndexManagerContainer.indexManagerContainer;

/**
 * Created by doctor on 2017/3/15.
 * 备份服务,负责写自身的索引和分发请求到其他节点
 * 数据写入主节点log, 然后主节点发送消息给其他节点
 */
public class ReplicateService {
    private Map<String, Client> nodeClients = new ConcurrentHashMap<>();
    private RoutingService routingService;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    
    private void submitReplicateTasks(String indexName, Message message) {
        List<RoutingService.NodeInfo> nodeInfos = routingService.getNodeInfos(indexName);
        for (RoutingService.NodeInfo nodeInfo : nodeInfos) {
            Client client = nodeClients.get(nodeInfo.getNodeId());
            executorService.submit(new ReplicateTask(client, message));
        }
    }

    public IndexResult createIndex(Message message) {
        CreateIndexRequest createIndexRequest = (CreateIndexRequest) message.getData();
        Schema schema = createIndexRequest.getSchema();
        if (schema == null) {
            schema = new Schema();
        }
        schema.setIndexName(createIndexRequest.getIndexName());
        indexManagerContainer.createIndex(schema);
        submitReplicateTasks(createIndexRequest.getIndexName(), message);
        return new IndexResult();
    }

    public RpcResult dropIndex(Message message) {
        String indexName = (String) message.getData();
        Schema schema = new Schema();
        schema.setIndexName(indexName);
        indexManagerContainer.dropIndex(schema);
        submitReplicateTasks(indexName, message);
        return new IndexResult();
    }

    public RpcResult putSchema(Message message) {
        Schema schema = (Schema) message.getData();
        indexManagerContainer.putSchema(schema);
        submitReplicateTasks(schema.getIndexName(), message);
        return null;
    }

    public RpcResult putAlias(Message message) {
        Tuple<String, String> alias = (Tuple<String, String>) message.getData();
        indexManagerContainer.putAlias(alias);
        submitReplicateTasks(alias.getT1(), message);
        return null;
    }

    public RpcResult dropAlias(Message message) {
        Tuple<String, String> alias = (Tuple<String, String>) message.getData();
        indexManagerContainer.dropAlias(alias);
        submitReplicateTasks(alias.getT1(), message);
        return null;
    }

    public RpcResult insertDocument(Message message) {
        InsertRequest insertRequest = (InsertRequest) message.getData();
        indexManagerContainer.insertDocument(insertRequest.getIndexName(), insertRequest.getDocument());
        submitReplicateTasks(insertRequest.getIndexName(), message);
        return null;
    }

    public RpcResult bulkInsert(Message message) {
        BulkRequest<Document> bulkRequest = (BulkRequest<Document>) message.getData();
        indexManagerContainer.bulkInsert(bulkRequest.getIndexName(), bulkRequest.getBulkData());
        submitReplicateTasks(bulkRequest.getIndexName(), message);
        return null;
    }

    public RpcResult deleteDocument(Message message) {
        DeleteRequest deleteRequest = (DeleteRequest) message.getData();
        indexManagerContainer.deleteDocument(deleteRequest.getIndexName(), deleteRequest.getDocId());
        submitReplicateTasks(deleteRequest.getIndexName(), message);
        return null;
    }

    public RpcResult bulkDelete(Message message) {
        BulkRequest<Long> bulkRequest = (BulkRequest<Long>) message.getData();
        indexManagerContainer.bulkDelete(bulkRequest.getIndexName(), bulkRequest.getBulkData());
        submitReplicateTasks(bulkRequest.getIndexName(), message);
        return null;
    }

    public RpcResult deleteByQuery(Message message) {
        QueryRequest queryRequest = (QueryRequest) message.getData();
        indexManagerContainer.deleteByQuery(queryRequest.getIndexName(), queryRequest.getQueryBuilder());
        submitReplicateTasks(queryRequest.getIndexName(), message);
        return null;
    }

    /**
     * Query and merge
     */
    public RpcResult Query(Message message) {
        QueryRequest queryRequest = (QueryRequest) message.getData();
        indexManagerContainer.query(queryRequest.getIndexName(), queryRequest.getQueryBuilder());
        submitReplicateTasks(queryRequest.getIndexName(), message);
        return null;
    }

    public class ReplicateTask implements Callable<RpcResult> {
        private Client client;
        private Message message;

        public ReplicateTask(Client client, Message message) {
            this.client = client;
            this.message = message;
        }

        @Override
        public RpcResult call() throws Exception {
            return client.sendMessage(message);
        }
    }
}
