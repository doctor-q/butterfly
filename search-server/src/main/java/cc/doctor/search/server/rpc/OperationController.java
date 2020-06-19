package cc.doctor.search.server.rpc;

import cc.doctor.search.client.rpc.Message;
import cc.doctor.search.client.rpc.operation.Operation;
import cc.doctor.search.client.rpc.result.RpcResult;
import cc.doctor.search.common.exceptions.rpc.UnSupportOperationException;
import cc.doctor.search.server.cluster.node.tolerance.ToleranceService;
import cc.doctor.search.server.cluster.replicate.ReplicateService;

import static cc.doctor.search.common.utils.Container.container;

/**
 * Created by doctor on 2017/3/14.
 */
public class OperationController {
    public static final OperationController operationController = new OperationController();

    private ReplicateService replicateService;
    private ToleranceService toleranceService;

    private OperationController() {
        replicateService = container.getComponent(ReplicateService.class);
        toleranceService = container.getComponent(ToleranceService.class);
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
            case FLUSH:
                return replicateService.flush(message);
            case QUERY:
                return replicateService.Query(message);
            case PING:
                return toleranceService.responsePing(message);
        }
        throw new UnSupportOperationException("UnSupport operation.");
    }
}

