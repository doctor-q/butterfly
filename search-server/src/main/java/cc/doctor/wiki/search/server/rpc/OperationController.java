package cc.doctor.wiki.search.server.rpc;

import cc.doctor.wiki.exceptions.rpc.UnSupportOperationException;
import cc.doctor.wiki.search.client.rpc.Message;
import cc.doctor.wiki.search.client.rpc.operation.Operation;
import cc.doctor.wiki.search.client.rpc.result.RpcResult;
import cc.doctor.wiki.search.server.cluster.node.tolerance.ToleranceService;
import cc.doctor.wiki.search.server.cluster.replicate.ReplicateService;

/**
 * Created by doctor on 2017/3/14.
 */
public class OperationController {
    public static final OperationController operationController = new OperationController();

    private ReplicateService replicateService;
    private ToleranceService toleranceService;

    private OperationController() {
    }

    public RpcResult handlerOperation(Message message) {
        Operation operation = message.getOperation();
        switch (operation) {
            case CREATE_INDEX:
                return replicateService.createIndex(message);
            case DROP_INDEX:
                return replicateService.dropIndex(message);
            case PUT_SCHEMA:
                return replicateService.putSchema(message);
            case PUT_ALIAS:
                return replicateService.putAlias(message);
            case DROP_ALIAS:
                return replicateService.dropAlias(message);
            case ADD_DOCUMENT:
                return replicateService.insertDocument(message);
            case BULK_INSERT:
                return replicateService.bulkInsert(message);
            case DELETE_DOCUMENT:
                return replicateService.deleteDocument(message);
            case BULK_DELETE:
                return replicateService.bulkDelete(message);
            case DELETE_BY_QUERY:
                return replicateService.deleteByQuery(message);
            case QUERY:
                return replicateService.Query(message);
            case PING:
                return toleranceService.responsePing(message);
            case MASTER_LOSS:
                return toleranceService.doMasterLoss(message);
            case NODE_LOSS:
                return toleranceService.doNodeLoss(message);
        }
        throw new UnSupportOperationException("UnSupport operation.");
    }
}

