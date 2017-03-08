package cc.doctor.wiki.search.server.index.store.indices.inverted;

/**
 * Created by doctor on 2017/3/7.
 * 倒排文件中存放的文档信息
 */
public class InvertedDoc {
    long docId;     //文档id
    long docFrequency;   //文档频率

    public InvertedDoc(long docId, long docFrequency) {
        this.docId = docId;
        this.docFrequency = docFrequency;
    }
}
