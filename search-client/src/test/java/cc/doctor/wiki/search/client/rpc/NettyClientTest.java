package cc.doctor.wiki.search.client.rpc;

import cc.doctor.wiki.search.client.rpc.operation.Operation;
import cc.doctor.wiki.search.client.query.QueryBuilder;
import cc.doctor.wiki.search.client.rpc.result.RpcResult;
import org.junit.Test;

/**
 * Created by doctor on 2017/3/15.
 */
public class NettyClientTest {
    @Test
    public void sendMessage() throws Exception {
        NettyClient nettyClient = new NettyClient();
        Message message = Message.newMessage().currentTimestamp().operation(Operation.QUERY).data(QueryBuilder.queryBuilder());
        RpcResult rpcResult = nettyClient.sendMessage(message);
        System.out.println(rpcResult);
    }

}