package cc.doctor.search.server.cluster.vote;

import cc.doctor.search.common.exceptions.cluster.VoteException;
import cc.doctor.search.common.ha.zk.ZookeeperClient;
import cc.doctor.search.server.common.config.GlobalConfig;
import cc.doctor.search.common.utils.StringUtils;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static cc.doctor.search.server.common.config.Settings.settings;

/**
 * Created by doctor on 2017/3/13.
 */
public class ZookeeperVote extends Vote {
    private ZookeeperClient zkClient;
    public static final String ZK_NODE_MASTER = "/es/metadata/master";
    private CountDownLatch masterCountDown = new CountDownLatch(1);

    public ZookeeperVote(VoteService voteService) {
        super(voteService);
        this.zkClient = ZookeeperClient.getClient(settings.getString(GlobalConfig.ZOOKEEPER_CONN_STRING));
    }

    @Override
    public VoteInfo voteMaster() {
        boolean masterExist = zkClient.existsNode(ZK_NODE_MASTER);
        if (masterExist) {
            String masterInfo = zkClient.readData(ZK_NODE_MASTER);
            VoteInfo voteInfo = decodeMasterInfo(masterInfo);
            if (voteInfo == null || voteInfo.getVoteVersion() < getVoteInfo().getVoteVersion()) {
                zkClient.writeData(ZK_NODE_MASTER, encodeMasterInfo(getVoteInfo()));
                return getVoteInfo();
            }
            return voteInfo;
        } else {
            boolean created = zkClient.createPathRecursion(ZK_NODE_MASTER, encodeMasterInfo(getVoteInfo()));
            if (created && zkClient.existsNode(ZK_NODE_MASTER)) {
                return getVoteInfo();
            }
        }
        return null;
    }

    @Override
    public VoteInfo abdicate() {
        zkClient.deleteNode(ZK_NODE_MASTER);
        try {
            masterCountDown.await();
        } catch (InterruptedException ignored) {
            return null;
        }
        boolean masterExist = zkClient.existsNode(ZK_NODE_MASTER);
        if (!masterExist) {
            return null;
        } else {
            String data = zkClient.readData(ZK_NODE_MASTER);
            return decodeMasterInfo(data);
        }
    }

    private VoteInfo decodeMasterInfo(String masterInfo) {
        if (masterInfo == null || masterInfo.isEmpty()) {
            return null;
        }
        VoteInfo voteInfo = new VoteInfo();
        Map<String, String> valuePair = StringUtils.toNameValuePair(masterInfo);
        String voteId = valuePair.get("voteId");
        String host = valuePair.get("host");
        String port = valuePair.get("port");
        String timestamp = valuePair.get("timestamp");
        if (voteId == null || host == null || port == null || timestamp == null) {
            throw new VoteException("Vote message error.");
        }
        voteInfo.setVoteId(voteId);
        voteInfo.setHost(host);
        voteInfo.setPort(Integer.parseInt(port));
        voteInfo.setTimestamp(Long.parseLong(timestamp));
        return voteInfo;
    }

    private String encodeMasterInfo(VoteInfo voteInfo) {
        if (voteInfo == null || voteInfo.getHost() == null) {
            return null;
        }
        return StringUtils.toNameValuePairString(voteInfo);
    }
}
