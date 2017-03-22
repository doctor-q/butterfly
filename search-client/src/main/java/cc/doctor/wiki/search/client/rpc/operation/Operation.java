package cc.doctor.wiki.search.client.rpc.operation;

/**
 * Created by doctor on 2017/3/8.
 * 操作
 */
public enum Operation {
    PING(0),
    CREATE_INDEX(1),
    DROP_INDEX(2),
    PUT_SCHEMA(0),
    PUT_ALIAS(0),
    DROP_ALIAS(0),
    ADD_DOCUMENT(3),
    BULK_INSERT(0),
    DELETE_DOCUMENT(4),
    BULK_DELETE(0),
    DELETE_BY_QUERY(0),
    QUERY(5),
    MASTER_LOSS(0),
    NODE_LOSS(0);
    int code;

    Operation(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Operation getOperation(int code) {
        for (Operation operation : Operation.values()) {
            if (operation.getCode() == code) {
                return operation;
            }
        }
        return null;
    }
}
