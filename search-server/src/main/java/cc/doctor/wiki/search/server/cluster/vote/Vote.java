package cc.doctor.wiki.search.server.cluster.vote;

import cc.doctor.wiki.search.server.cluster.routing.RoutingNode;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;

import java.util.Date;

import static cc.doctor.wiki.search.server.common.config.Settings.settings;

/**
 * Created by doctor on 2017/3/13.
 */
public abstract class Vote {
    private VoteInfo voteInfo;
    private VoteIdStrategy voteIdStrategy;
    private VoteService voteService;

    public Vote(VoteService voteService) {
        this.voteService = voteService;
        voteIdStrategy = new DefaultVoteIdStrategy();
    }

    public Vote(VoteService voteService, VoteIdStrategy voteIdStrategy) {
        this.voteService = voteService;
        this.voteIdStrategy = voteIdStrategy;
    }

    public void setVoteInfo(VoteInfo voteInfo) {
        this.voteInfo = voteInfo;
    }

    public VoteInfo getVoteInfo() {
        return voteInfo;
    }

    /**
     * 推举自己为主节点,返回选举结果信息,如果选举结果id与自身携带的id相同则表明自己是主节点
     */
    public abstract VoteInfo voteMaster();

    /**
     * 禅让主节点
     * @return 返回新的主节点信息
     */
    public abstract VoteInfo abdicate();

    public VoteInfo newVoteInfo() {
        VoteInfo voteInfo = new VoteInfo();
        voteInfo.setVoteId(voteIdStrategy.getVoteId());
        voteInfo.setHost(settings.getString(GlobalConfig.NETTY_SERVER_HOST));
        voteInfo.setPort(settings.getInt(GlobalConfig.NETTY_SERVER_PORT));
        voteInfo.setTimestamp(new Date().getTime());
        voteInfo.setVoteVersion(0);
        return voteInfo;
    }

    public String getVoteId() {
        return voteIdStrategy.getVoteId();
    }

    /**
     * 选主信息,voteId代表参与选主的id,
     * voteVersion表示参与选主的版本号,主节点初始启动后版本号为0,每当新一轮选主,版本号加一
     */
    public static class VoteInfo {
        private String voteId;
        private long voteVersion;
        private String host;
        private int port;
        private long timestamp;

        public String getVoteId() {
            return voteId;
        }

        public void setVoteId(String voteId) {
            this.voteId = voteId;
        }

        public long getVoteVersion() {
            return voteVersion;
        }

        public void setVoteVersion(long voteVersion) {
            this.voteVersion = voteVersion;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    interface VoteIdStrategy {

        String getVoteId();
    }

    public class DefaultVoteIdStrategy implements VoteIdStrategy {
        @Override
        public String getVoteId() {
            return voteService.getNode().getRoutingNode().getNodeId();
        }
    }
}
