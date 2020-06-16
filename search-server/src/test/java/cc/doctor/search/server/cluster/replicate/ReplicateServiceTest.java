package cc.doctor.search.server.cluster.replicate;

import cc.doctor.search.common.schema.Schema;
import cc.doctor.search.common.document.Document;
import cc.doctor.search.common.document.Field;
import cc.doctor.search.client.rpc.Message;
import cc.doctor.search.client.rpc.operation.Operation;
import cc.doctor.search.client.rpc.request.BulkRequest;
import cc.doctor.search.client.rpc.request.CreateIndexRequest;
import cc.doctor.search.client.rpc.request.IndexRequest;
import cc.doctor.search.server.cluster.node.Node;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by doctor on 17-4-5.
 */
public class ReplicateServiceTest {
    ReplicateService replicateService;
    Node node;

    @Before
    public void setup() {
        node = new Node();
//        node.start();
        node.getRoutingService().loadRoutingNodes();
        node.setRoutingNode(node.getRoutingService().getRoutingNodes().get(0));
        replicateService = new ReplicateService(node);
    }

    @Test
    public void createIndex() throws Exception {
        Schema schema = new Schema();
        schema.setShards(1);
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("order_info").schema(schema);
        Message message = Message.newMessage().currentTimestamp().operation(Operation.CREATE_INDEX).data(createIndexRequest);
        replicateService.createIndex(message);
    }

    @Test
    public void dropIndex() throws Exception {
    }

    @Test
    public void putSchema() throws Exception {
    }

    @Test
    public void putAlias() throws Exception {
    }

    @Test
    public void dropAlias() throws Exception {
    }

    @Test
    public void insertDocument() throws Exception {
    }

    @Test
    public void bulkInsert() throws Exception {
        node.getSchemaService().loadSchemas();
        node.getIndexManagerService().loadIndexes();
        BulkRequest<Document> bulkRequest = new BulkRequest<>("order_info");
        List<Document> documents = new LinkedList<>();
        for (int i = 0; i < 1000; i++) {
            Document document = new Document();
            document.field(new Field("id", i)).field(new Field("name", "name" + i));
            documents.add(document);
        }
        bulkRequest.setBulkData(documents);
        Message message = Message.newMessage().data(bulkRequest);
        replicateService.bulkInsert(message);
        Thread.sleep(10000);
        IndexRequest indexRequest = new IndexRequest("order_info");
        message = Message.newMessage().data(indexRequest);
        replicateService.flush(message);
    }

    @Test
    public void deleteDocument() throws Exception {
    }

    @Test
    public void bulkDelete() throws Exception {
    }

    @Test
    public void deleteByQuery() throws Exception {
    }

    @Test
    public void query() throws Exception {
    }

}