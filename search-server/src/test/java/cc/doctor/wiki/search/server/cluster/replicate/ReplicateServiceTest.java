package cc.doctor.wiki.search.server.cluster.replicate;

import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.client.rpc.Message;
import cc.doctor.wiki.search.client.rpc.operation.Operation;
import cc.doctor.wiki.search.client.rpc.request.CreateIndexRequest;
import cc.doctor.wiki.search.client.rpc.result.IndexResult;
import cc.doctor.wiki.search.server.cluster.node.Node;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

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