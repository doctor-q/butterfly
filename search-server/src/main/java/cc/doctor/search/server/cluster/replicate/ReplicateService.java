package cc.doctor.search.server.cluster.replicate;

import cc.doctor.search.client.rpc.request.*;
import cc.doctor.search.client.rpc.result.*;
import cc.doctor.search.server.cluster.node.Node;
import cc.doctor.search.server.cluster.node.NodeClientHolder;
import cc.doctor.search.server.cluster.node.schema.SchemaService;
import cc.doctor.search.client.route.RoutingNode;
import cc.doctor.search.client.route.RoutingService;
import cc.doctor.search.server.common.config.GlobalConfig;
import cc.doctor.search.server.common.config.Settings;
import cc.doctor.search.server.index.manager.AllIndexService;
import cc.doctor.search.server.recovery.RecoveryService;
import cc.doctor.search.common.entity.Action;
import cc.doctor.search.common.entity.Tuple;
import cc.doctor.search.common.ha.zk.ZookeeperClient;
import cc.doctor.search.common.document.Document;
import cc.doctor.search.common.schema.Schema;
import cc.doctor.search.client.rpc.Client;
import cc.doctor.search.client.rpc.Message;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cc.doctor.search.common.utils.Container.container;

/**
 * Created by doctor on 2017/3/15.
 * 备份服务,负责写自身的索引和分发请求到其他节点
 * 数据写入主节点log, 然后主节点发送消息给其他节点
 */
public class ReplicateService {
    private RoutingService routingService;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private NodeAllocator nodeAllocator;
    private Node node;
    private SchemaService schemaService;
    private RecoveryService recoveryService;
    private NodeClientHolder nodeClientHolder;
    private AllIndexService allIndexService;

    public ReplicateService(Node node) {
        this.node = node;
        schemaService = container.getComponent(SchemaService.class);
        routingService = container.getComponent(RoutingService.class);
        nodeAllocator = new NodeAllocator(routingService, ZookeeperClient.getClient((String) Settings.settings.get(GlobalConfig.ZOOKEEPER_CONN_STRING)));
        recoveryService = container.getComponent(RecoveryService.class);
        nodeClientHolder = container.getComponent(NodeClientHolder.class);
        allIndexService = container.getComponent(AllIndexService.class);
    }

    private void submitReplicateTasks(String indexName, Message message, Action action) {
        List<RoutingNode> indexRoutingNodes = routingService.getIndexRoutingNodes(indexName);
        for (RoutingNode routingNode : indexRoutingNodes) {
            if (routingNode.getNodeName().equals(node.getRoutingNode().getNodeName())) {
                action.doAction();
            } else {
                Client client = nodeClientHolder.getNodeClient(routingNode.getNodeName());
                executorService.submit(new ReplicateTask(client, message));
            }
        }
    }

    /**
     * 创建索引
     * 1. 分配shard
     * 2. 设置schema
     */
    public IndexResult createIndex(Message message) {
        CreateIndexRequest createIndexRequest = (CreateIndexRequest) message.getData();
        Schema schema = createIndexRequest.getSchema();
        if (schema == null) {
            schema = new Schema();
        }
        schema.setIndexName(createIndexRequest.getIndexName());
        //分配节点和分片
        nodeAllocator.allocateNodes(schema.getReplicate(), schema.getShards(), schema.getIndexName());
        //Put schema
        schemaService.putSchema(schema);
        if (routingService.containsIndexNode(node.getRoutingNode())) {
            recoveryService.createIndexOperation(schema);
        }
        final Schema finalSchema = schema;
        submitReplicateTasks(createIndexRequest.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                allIndexService.createIndex(finalSchema);
                routingService.updateRoutingInfo();
            }
        });
        return new IndexResult();
    }

    /**
     * 删除索引
     */
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
                allIndexService.dropIndex(schema);
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
                allIndexService.putSchema(schema);
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
                allIndexService.putAlias(alias);
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
                allIndexService.dropAlias(alias);
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
                allIndexService.insertDocument(insertRequest.getIndexName(), insertRequest.getDocument());
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
                allIndexService.bulkInsert(bulkRequest.getIndexName(), bulkRequest.getBulkData());
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
                allIndexService.deleteDocument(deleteRequest.getIndexName(), deleteRequest.getDocId());
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
                allIndexService.bulkDelete(bulkRequest.getIndexName(), bulkRequest.getBulkData());
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
                allIndexService.deleteByQuery(queryRequest.getIndexName(), queryRequest.getQueryBuilder());
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
                allIndexService.query(queryRequest.getIndexName(), queryRequest.getQueryBuilder());
            }
        });
        return new SearchResult();
    }

    public RpcResult flush(Message message) {
        final IndexRequest indexRequest = (IndexRequest) message.getData();
        recoveryService.flushOperation(indexRequest.getIndexName());
        submitReplicateTasks(indexRequest.getIndexName(), message, new Action() {
            @Override
            public void doAction() {
                allIndexService.flush(indexRequest.getIndexName());
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
