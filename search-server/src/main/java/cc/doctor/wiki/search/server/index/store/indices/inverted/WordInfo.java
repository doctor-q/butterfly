package cc.doctor.wiki.search.server.index.store.indices.inverted;

/**
 * Created by doctor on 2017/3/7.
 * 存储倒排表的位置信息
 */
public class WordInfo {
    private InvertedNode invertedNode;  //记录倒排链信息的节点
    private long frequency; //词频

    public WordInfo(InvertedNode invertedNode) {
        this.invertedNode = invertedNode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WordInfo) {
            WordInfo wordInfo = (WordInfo) obj;
            return wordInfo.invertedNode.position == this.invertedNode.position;
        }
        return false;
    }

    /**
     * 倒排链物理结构,有数据区和空闲区写,当空闲区写满后,读出倒排链后增加版本号,扩展倒排链大小追加到文件尾
     */
    public static class InvertedNode {
        private Object data;    //索引字段保存在内存
        private long version;   //当前倒排链的版本号
        private int position;   //倒排链的文件位置的起始位置
        private int size;       //倒排链的数据大小
        private int free;       //空余空间

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public long getVersion() {
            return version;
        }

        public void setVersion(long version) {
            this.version = version;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getFree() {
            return free;
        }

        public void setFree(int free) {
            this.free = free;
        }

        public InvertedNode(Object data, int position, int size) {
            this.position = position;
            this.size = size;
        }
    }
}
