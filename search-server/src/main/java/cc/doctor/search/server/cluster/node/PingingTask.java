package cc.doctor.search.server.cluster.node;

import cc.doctor.search.server.cluster.node.tolerance.ToleranceService;
import cc.doctor.search.server.cluster.routing.RoutingNode;
import cc.doctor.search.server.cluster.routing.RoutingService;
import cc.doctor.search.server.common.config.GlobalConfig;
import cc.doctor.search.server.common.config.Settings;
import cc.doctor.search.common.exceptions.rpc.TimeoutException;
import cc.doctor.search.common.schedule.Schedule;
import cc.doctor.search.common.schedule.Task;
import cc.doctor.search.client.rpc.Client;
import cc.doctor.search.client.rpc.Message;
import cc.doctor.search.client.rpc.operation.Operation;
import cc.doctor.search.client.rpc.result.PingResult;
import cc.doctor.search.client.rpc.result.RpcResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cc.doctor.search.common.utils.Container.container;

/**
 * Created by doctor on 2017/3/20.
 * TODO 设置集群健康机制,检查集群健康
 */
@Schedule(duration = 60 * 1000, name = "pinging")
public class PingingTask implements Task {
    private final long pingTimeout = 30 * 1000;
    private Map<String, Client> nodeClients = new ConcurrentHashMap<>();
    private Map<String, Integer> nodeFailds = new HashMap<>();
    private RoutingService routingService = container.getComponent(RoutingService.class);
    private ToleranceService toleranceService = container.getComponent(ToleranceService.class);
    public static final int maxLossConnectionTimes = Settings.settings.getInt(GlobalConfig.MAX_LOSS_CONNECTION_TIMES);

    @Override
    public RpcResult run() {
        for (String nodeName : nodeClients.keySet()) {
            Message message = Message.newMessage().currentTimestamp().operation(Operation.PING);
            PingResult pingResult;
            try {
                pingResult = (PingResult) nodeClients.get(nodeName).sendMessage(message, pingTimeout);
            } catch (TimeoutException e) {                //if timeout
                Integer failds = nodeFailds.get(nodeName);
                if (failds == null) {
                    failds = 0;
                }
                failds ++;
                nodeFailds.put(nodeName, failds);
            }
        }
        RoutingNode master = routingService.getMaster();
        for (String nodeName : nodeFailds.keySet()) {
            //当从属节点被检测到丢失,从属节点向主节点汇报,主节点收到过半的丢失汇报后确认从属节点丢失
            //当主节点检测到丢失后,从属节点向所有从属节点汇报,当从属节点收到过半的汇报后从新选主
            if (nodeFailds.get(nodeName) > maxLossConnectionTimes) {
                if (master.getNodeName().equals(nodeName)) {
                    toleranceService.reportMasterLoss();
                } else {
                    toleranceService.reportSlaveLoss();
                }
            }
        }
        return null;
    }

    @Override
    public void callback(Object result) {

    }
}
