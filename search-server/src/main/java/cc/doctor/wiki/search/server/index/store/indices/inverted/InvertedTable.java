package cc.doctor.wiki.search.server.index.store.indices.inverted;

import java.io.Serializable;

/**
 * Created by doctor on 2017/3/7.
 * 倒排文件中存放的文档信息
 */
public class InvertedTable implements Serializable {
    private static final long serialVersionUID = -8762176665798767763L;
    private WordInfo.InvertedNode invertedNode;
    private Iterable<InvertedDoc> invertedDocs;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public WordInfo.InvertedNode getInvertedNode() {
        return invertedNode;
    }

    public void setInvertedNode(WordInfo.InvertedNode invertedNode) {
        this.invertedNode = invertedNode;
    }

    public Iterable<InvertedDoc> getInvertedDocs() {
        return invertedDocs;
    }

    public void setInvertedDocs(Iterable<InvertedDoc> invertedDocs) {
        this.invertedDocs = invertedDocs;
    }

    public static class InvertedDoc {
        long docId;     //文档id
        long docFrequency;   //文档频率

        public InvertedDoc(long docId, long docFrequency) {
            this.docId = docId;
            this.docFrequency = docFrequency;
        }
    }
}
