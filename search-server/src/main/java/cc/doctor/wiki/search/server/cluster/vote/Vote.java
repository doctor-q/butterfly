package cc.doctor.wiki.search.server.cluster.vote;

/**
 * Created by doctor on 2017/3/13.
 */
public abstract class Vote {
    private VoteInfo voteInfo;
    private VoteIdStrategy voteIdStrategy;

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

    static class VoteInfo {
        private long voteId;
        private String host;
        private String port;
        private long timestamp;

        public long getVoteId() {
            return voteId;
        }

        public void setVoteId(long voteId) {
            this.voteId = voteId;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    interface VoteIdStrategy {}
}
