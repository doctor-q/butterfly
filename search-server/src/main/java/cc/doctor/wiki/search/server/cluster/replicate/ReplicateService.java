package cc.doctor.wiki.search.server.cluster.replicate;

import cc.doctor.wiki.common.Action;
import cc.doctor.wiki.common.Tuple;
import cc.doctor.wiki.ha.zk.ZookeeperClient;
import cc.doctor.wiki.index.document.Document;
import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.client.rpc.Client;
import cc.doctor.wiki.search.client.rpc.Message;
import cc.doctor.wiki.search.client.rpc.request.*;
import cc.doctor.wiki.search.client.rpc.result.IndexResult;
import cc.doctor.wiki.search.client.rpc.result.RpcResult;
import cc.doctor.wiki.search.server.cluster.node.Node;
import cc.doctor.wiki.search.server.cluster.routing.RoutingNode;
import cc.doctor.wiki.search.server.cluster.routing.RoutingService;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cc.doctor.wiki.search.server.common.config.Settings.settings;
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
    private NodeAllocator nodeAllocator;
    private Node node;

    public ReplicateService() {
        routingService = new RoutingService();
        nodeAllocator = new NodeAllocator(routingService, ZookeeperClient.getClient(settings.get(GlobalConfig.ZOOKEEPER_CONN_STRING)));
    }
    
    private void submitReplicateTasks(String indexName, Message message, Action action) {
        List<RoutingNode> indexRoutingNodes = routingService.getIndexRoutingNodes(indexName);
        for (RoutingNode routingNode : indexRoutingNodes) {
            if (routingNode.getNodeId().equals(node.getRoutingNode().getNodeId())) {
                action.doAction();
            } else {
                Client client = nodeClients.get(routingNode.getNodeId());
                executorService.submit(new ReplicateTask(client, message));
            }
        }
    }

    public IndexResult createIndex(Message message) {
        CreateIndexRequest createIndexRequest = (CreateIndexRequest) message.getData();
        Schema schema = createIndexRequest.getSchema();
        if (schema == null) {
            schema = new Schema();
        }
        schema.setIndexName(createIndexRequest.getIndexName());
        nodeAllocator.allocateNodes(schema.getReplicate(), schema.getShards(), schema.getIndexName());
        final Schema finalSchema = schema;
        submitReplicateTasks(createIndexRequest.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.createIndex(finalSchema);
            }
        });
        return new IndexResult();
    }

    public RpcResult dropIndex(Message message) {
        String indexName = (String) message.getData();
        final Schema schema = new Schema();
        schema.setIndexName(indexName);
        submitReplicateTasks(indexName, message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.dropIndex(schema);
            }
        });
        return new IndexResult();
    }

    public RpcResult putSchema(Message message) {
        final Schema schema = (Schema) message.getData();
        submitReplicateTasks(schema.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.putSchema(schema);
            }
        });
        return null;
    }

    public RpcResult putAlias(Message message) {
        final Tuple<String, String> alias = (Tuple<String, String>) message.getData();
        submitReplicateTasks(alias.getT1(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.putAlias(alias);
            }
        });
        return null;
    }

    public RpcResult dropAlias(Message message) {
        final Tuple<String, String> alias = (Tuple<String, String>) message.getData();
        submitReplicateTasks(alias.getT1(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.dropAlias(alias);
            }
        });
        return null;
    }

    public RpcResult insertDocument(Message message) {
        final InsertRequest insertRequest = (InsertRequest) message.getData();
        submitReplicateTasks(insertRequest.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.insertDocument(insertRequest.getIndexName(), insertRequest.getDocument());
            }
        });
        return null;
    }

    public RpcResult bulkInsert(Message message) {
        final BulkRequest<Document> bulkRequest = (BulkRequest<Document>) message.getData();
        submitReplicateTasks(bulkRequest.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.bulkInsert(bulkRequest.getIndexName(), bulkRequest.getBulkData());
            }
        });
        return null;
    }

    public RpcResult deleteDocument(Message message) {
        final DeleteRequest deleteRequest = (DeleteRequest) message.getData();
        submitReplicateTasks(deleteRequest.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.deleteDocument(deleteRequest.getIndexName(), deleteRequest.getDocId());
            }
        });
        return null;
    }

    public RpcResult bulkDelete(Message message) {
        final BulkRequest<Long> bulkRequest = (BulkRequest<Long>) message.getData();
        submitReplicateTasks(bulkRequest.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.bulkDelete(bulkRequest.getIndexName(), bulkRequest.getBulkData());
            }
        });
        return null;
    }

    public RpcResult deleteByQuery(Message message) {
        final QueryRequest queryRequest = (QueryRequest) message.getData();
        submitReplicateTasks(queryRequest.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.deleteByQuery(queryRequest.getIndexName(), queryRequest.getQueryBuilder());
            }
        });
        return null;
    }

    /**
     * Query and merge
     */
    public RpcResult Query(Message message) {
        final QueryRequest queryRequest = (QueryRequest) message.getData();
        submitReplicateTasks(queryRequest.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.query(queryRequest.getIndexName(), queryRequest.getQueryBuilder());
            }
        });
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
