package cc.doctor.wiki.search.server.cluster.node;

import cc.doctor.wiki.schedule.Schedule;
import cc.doctor.wiki.schedule.Task;
import cc.doctor.wiki.search.client.rpc.Client;
import cc.doctor.wiki.search.client.rpc.Message;
import cc.doctor.wiki.search.client.rpc.operation.Operation;
import cc.doctor.wiki.search.client.rpc.result.RpcResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by doctor on 2017/3/20.
 * TODO 设置集群健康机制,检查集群健康
 */
@Schedule(duration = 60 * 1000, name = "pinging")
public class PingingTask implements Task {
    private final long pingTimeout = 30 * 1000;
    private Map<String, Client> nodeClients = new ConcurrentHashMap<>();
    private Map<String, Integer> nodeFailds = new HashMap<>();

    @Override
    public RpcResult run() {
        for (Client client : nodeClients.values()) {
            Message message = Message.newMessage().currentTimestamp().operation(Operation.PING);
            RpcResult rpcResult = client.sendMessage(message, pingTimeout);
            //if timeout

        }
        return null;
    }

    @Override
    public void callback(Object result) {

    }
}
