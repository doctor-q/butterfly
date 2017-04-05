package cc.doctor.wiki.search.server.cluster.replicate;

import cc.doctor.wiki.common.Action;
import cc.doctor.wiki.common.Tuple;
import cc.doctor.wiki.ha.zk.ZookeeperClient;
import cc.doctor.wiki.search.client.query.document.Document;
import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.client.rpc.Client;
import cc.doctor.wiki.search.client.rpc.Message;
import cc.doctor.wiki.search.client.rpc.request.*;
import cc.doctor.wiki.search.client.rpc.result.*;
import cc.doctor.wiki.search.server.cluster.node.Node;
import cc.doctor.wiki.search.server.cluster.routing.RoutingNode;
import cc.doctor.wiki.search.server.cluster.routing.RoutingService;
import cc.doctor.wiki.search.server.common.Container;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.search.server.index.store.indices.recovery.RecoveryService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cc.doctor.wiki.search.server.common.Container.container;
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
    private RecoveryService recoveryService;

    public ReplicateService(Node node) {
        this.node = node;
        routingService = container.getComponent(RoutingService.class);
        nodeAllocator = new NodeAllocator(routingService, ZookeeperClient.getClient((String) settings.get(GlobalConfig.ZOOKEEPER_CONN_STRING)));
        recoveryService = container.getComponent(RecoveryService.class);
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
        if (routingService.containsIndexNode(node.getRoutingNode())) {
            recoveryService.createIndexOperation(schema);
        }
        final Schema finalSchema = schema;
        submitReplicateTasks(createIndexRequest.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.createIndex(finalSchema);
            }

            @Override
            public void callback() {
                //保存索引信息
                routingService.updateRoutingInfo();
            }
        });
        return new IndexResult();
    }

    public RpcResult dropIndex(Message message) {
        String indexName = (String) message.getData();
        final Schema schema = new Schema();
        schema.setIndexName(indexName);
        if (routingService.containsIndexNode(node.getRoutingNode())) {
            recoveryService.dropIndexOperation(schema);
        }
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
        if (routingService.containsIndexNode(node.getRoutingNode())) {
            recoveryService.putSchemaOperation(schema);
        }
        submitReplicateTasks(schema.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.putSchema(schema);
            }
        });
        return new IndexResult();
    }

    public RpcResult putAlias(Message message) {
        final Tuple<String, String> alias = (Tuple<String, String>) message.getData();
        if (routingService.containsIndexNode(node.getRoutingNode())) {
            recoveryService.putAliasOperation(alias);
        }
        submitReplicateTasks(alias.getT1(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.putAlias(alias);
            }
        });
        return new IndexResult();
    }

    public RpcResult dropAlias(Message message) {
        final Tuple<String, String> alias = (Tuple<String, String>) message.getData();
        if (routingService.containsIndexNode(node.getRoutingNode())) {
            recoveryService.dropAliasOperaion(alias);
        }
        submitReplicateTasks(alias.getT1(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.dropAlias(alias);
            }
        });
        return new IndexResult();
    }

    public RpcResult insertDocument(Message message) {
        final InsertRequest insertRequest = (InsertRequest) message.getData();
        if (routingService.containsIndexNode(node.getRoutingNode())) {
            recoveryService.insertDocumentOperation(insertRequest.getIndexName(), insertRequest.getDocument());
        }
        submitReplicateTasks(insertRequest.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.insertDocument(insertRequest.getIndexName(), insertRequest.getDocument());
            }
        });
        return new InsertResult();
    }

    public RpcResult bulkInsert(Message message) {
        final BulkRequest<Document> bulkRequest = (BulkRequest<Document>) message.getData();
        recoveryService.bulkInsertOperation(bulkRequest.getIndexName(), bulkRequest.getBulkData());
        submitReplicateTasks(bulkRequest.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.bulkInsert(bulkRequest.getIndexName(), bulkRequest.getBulkData());
            }
        });
        return new BulkResult();
    }

    public RpcResult deleteDocument(Message message) {
        final DeleteRequest deleteRequest = (DeleteRequest) message.getData();
        if (routingService.containsIndexNode(node.getRoutingNode())) {
            recoveryService.deleteDocumentOperation(deleteRequest.getIndexName(), deleteRequest.getDocId());
        }
        submitReplicateTasks(deleteRequest.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.deleteDocument(deleteRequest.getIndexName(), deleteRequest.getDocId());
            }
        });
        return new DeleteResult();
    }

    public RpcResult bulkDelete(Message message) {
        final BulkRequest<Long> bulkRequest = (BulkRequest<Long>) message.getData();
        if (routingService.containsIndexNode(node.getRoutingNode())) {
            recoveryService.bulkDelete(bulkRequest.getIndexName(), bulkRequest.getBulkData());
        }
        submitReplicateTasks(bulkRequest.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.bulkDelete(bulkRequest.getIndexName(), bulkRequest.getBulkData());
            }
        });
        return new BulkResult();
    }

    public RpcResult deleteByQuery(Message message) {
        final QueryRequest queryRequest = (QueryRequest) message.getData();
        if (routingService.containsIndexNode(node.getRoutingNode())) {
            recoveryService.deleteByQueryOperation(queryRequest.getIndexName(), queryRequest.getQueryBuilder());
        }
        submitReplicateTasks(queryRequest.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.deleteByQuery(queryRequest.getIndexName(), queryRequest.getQueryBuilder());
            }
        });
        return new BulkResult();
    }

    /**
     * Query and merge
     */
    public RpcResult Query(Message message) {
        final QueryRequest queryRequest = (QueryRequest) message.getData();
        recoveryService.queryOperation(queryRequest.getIndexName(), queryRequest.getQueryBuilder());
        submitReplicateTasks(queryRequest.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                indexManagerContainer.query(queryRequest.getIndexName(), queryRequest.getQueryBuilder());
            }
        });
        return new SearchResult();
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
