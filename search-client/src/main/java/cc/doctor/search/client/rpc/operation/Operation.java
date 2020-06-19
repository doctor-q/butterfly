package cc.doctor.search.client.rpc.operation;

import lombok.AllArgsConstructor;

/**
 * Created by doctor on 2017/3/8.
 * 操作
 */
@AllArgsConstructor
public enum Operation {
    PING(true),
    CREATE_INDEX(true),
    DROP_INDEX(true),
    PUT_SCHEMA(true),
    PUT_ALIAS(true),
    DROP_ALIAS(true),
    ADD_DOCUMENT(false),
    BULK_INSERT(false),
    DELETE_DOCUMENT(false),
    BULK_DELETE(false),
    DELETE_BY_QUERY(false),
    FLUSH(false),
    QUERY(false);

    /**
     * transform with master
     */
    private boolean master;
}
