package cc.doctor.wiki.search.server.cluster.replicate;

import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.client.query.document.Document;
import cc.doctor.wiki.search.client.query.document.Field;
import cc.doctor.wiki.search.client.rpc.Message;
import cc.doctor.wiki.search.client.rpc.operation.Operation;
import cc.doctor.wiki.search.client.rpc.request.BulkRequest;
import cc.doctor.wiki.search.client.rpc.request.CreateIndexRequest;
import cc.doctor.wiki.search.client.rpc.request.IndexRequest;
import cc.doctor.wiki.search.client.rpc.result.IndexResult;
import cc.doctor.wiki.search.server.cluster.node.Node;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by doctor on 17-4-5.
 */
public class ReplicateServiceTest {
    ReplicateService replicateService;
    @Before
    public void setup() {
        Node node = new Node();
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
        IndexRequest indexRequest = new IndexRequest("order_info");
        message = Message.newMessage().data(indexRequest);
        replicateService.flush(message);
        Thread.sleep(1000);
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