package cc.doctor.wiki.search.server.rpc;

import cc.doctor.wiki.search.client.rpc.result.RpcResult;

/**
 * Created by doctor on 2017/3/14.
 */
public interface OperationHandler {
    RpcResult handlerOperation(Object data);
}

/**
 * 查询处理
 */
class QueryHandler implements OperationHandler {

    @Override
    public RpcResult handlerOperation(Object data) {
        return null;
    }
}

/**
 * 文档管理,负责文档的增删
 */
class DocumentHandler implements OperationHandler {

    @Override
    public RpcResult handlerOperation(Object data) {
        return null;
    }
}

/**
 * 索引管理
 */
class IndexHandler implements OperationHandler {

    @Override
    public RpcResult handlerOperation(Object data) {
        return null;
    }
}

