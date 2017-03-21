package cc.doctor.wiki.search.server.index.store.indices.inverted;

/**
 * Created by doctor on 2017/3/7.
 * 存储倒排表的位置信息
 */
public class WordInfo {
    private long frequency; //词频
    private Object data;    //索引字段保存在内存
    private long version;   //当前倒排链的版本号
    private long position;   //倒排链的文件位置的起始位置

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WordInfo) {
            WordInfo wordInfo = (WordInfo) obj;
            return wordInfo.position == this.position;
        }
        return false;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

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

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

}
