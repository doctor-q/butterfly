package cc.doctor.wiki.search.server.index.store.indices.inverted;

/**
 * Created by doctor on 2017/3/7.
 * 存储倒排表的位置信息
 */
public class WordInfo {
    private long position;  //倒排链位置
    private Object data;    //索引字段保存在内存
    long frequency; //词频

    public WordInfo(long position, Object data) {
        this.position = position;
        this.data = data;
    }
}
