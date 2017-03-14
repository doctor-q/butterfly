package cc.doctor.wiki.search.server.rpc;

import cc.doctor.wiki.exceptions.rpc.UnSupportOperationException;
import cc.doctor.wiki.protocol.operation.Operation;

/**
 * Created by doctor on 2017/3/14.
 */
public class OperationHandlerFactory {
    public static OperationHandler getOperationHandler(Operation operation) {
        switch (operation) {
            case CREATE_INDEX:
            case DELETE_INDEX:
            case PUT_SCHEMA:
                return new IndexHandler();
            case ADD_DOCUMENT:
            case DELETE_DOCUMENT:
                return new DocumentHandler();
            case QUERY:
                return new QueryHandler();
        }
        throw new UnSupportOperationException("UnSupport operation.");
    }
}
