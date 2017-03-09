package cc.doctor.wiki.operation;

/**
 * Created by doctor on 2017/3/8.
 * 操作
 */
public enum Operation {
    CREATE_INDEX(1), DELETE_INDEX(2),PUT_SCHEMA(0), ADD_DOCUMENT(3), DELETE_DOCUMENT(4), QUERY(5);
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
