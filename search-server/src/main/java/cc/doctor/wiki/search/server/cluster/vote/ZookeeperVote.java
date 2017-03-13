package cc.doctor.wiki.search.server.cluster.vote;

import cc.doctor.wiki.ha.zk.ZookeeperClient;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.concurrent.CountDownLatch;

/**
 * Created by doctor on 2017/3/13.
 */
public class ZookeeperVote extends Vote {
    private ZookeeperClient zkClient;
    public static final String ZK_NODE_MASTER = "/es/metadata/master";
    private CountDownLatch masterCountDown = new CountDownLatch(1);

    public ZookeeperVote(ZookeeperClient zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public VoteInfo voteMaster() {
        zkClient.createConnection();
        boolean masterExist = zkClient.existsNode(ZK_NODE_MASTER);
        if (masterExist) {
            String masterInfo = zkClient.readData(ZK_NODE_MASTER);
            return decodeMasterInfo(masterInfo);
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
        masterInfo = URLDecoder.decode(masterInfo);
        String[] split = masterInfo.split("&");
        for (String nameValue : split) {
            String[] nameValuePair = nameValue.split("=");
            if (nameValuePair.length == 2) {
                if (nameValuePair[0].equals("voteId")) {
                    voteInfo.setVoteId(Long.parseLong(nameValuePair[1]));
                } else if (nameValuePair[0].equals("host")) {
                    voteInfo.setHost(nameValuePair[1]);
                } else if (nameValuePair[0].equals("port")) {
                    voteInfo.setPort(nameValuePair[1]);
                } else if (nameValuePair[0].equals("timestamp")) {
                    voteInfo.setTimestamp(Long.parseLong(nameValuePair[1]));
                }
            }
        }
        return voteInfo;
    }

    private String encodeMasterInfo(VoteInfo voteInfo) {
        if (voteInfo == null || voteInfo.getHost() == null || voteInfo.getPort() == null) {
            return null;
        }
        return URLEncoder.encode("voteId=" + voteInfo.getVoteId() + "&host=" + voteInfo.getHost() + "&port=" + voteInfo.getPort() + "&timestamp=" + voteInfo.getTimestamp());
    }
}
