package cc.doctor.wiki.search.server.index.store.indices.inverted;

import java.io.Serializable;
import java.util.List;

/**
 * Created by doctor on 2017/3/7.
 * 倒排文件中存放的文档信息
 */
public class InvertedTable implements Serializable {
    private static final long serialVersionUID = -8762176665798767763L;
    private WordInfo.InvertedNode invertedNode;
    private List<InvertedDoc> invertedDocs;

    public WordInfo.InvertedNode getInvertedNode() {
        return invertedNode;
    }

    public void setInvertedNode(WordInfo.InvertedNode invertedNode) {
        this.invertedNode = invertedNode;
    }

    public List<InvertedDoc> getInvertedDocs() {
        return invertedDocs;
    }

    public void setInvertedDocs(List<InvertedDoc> invertedDocs) {
        this.invertedDocs = invertedDocs;
    }

    public InvertedDoc getInvertedDoc(Long docId) {
        int size = invertedDocs.size();
        int left = 0;
        int middle = size / 2;
        int right = size - 1;
        while (middle != left) {
            InvertedDoc invertedDoc = invertedDocs.get(middle);
            if (invertedDoc.getDocId() > docId) {
                right = middle;
                middle = (left + right) / 2;
            } else if (invertedDoc.getDocId() < docId) {
                left = middle;
                middle = (left + right) / 2;
            } else {
                return invertedDoc;
            }
        }
        return null;
    }

    public void addInvertedDoc(InvertedDoc invertedDoc) {
        int size = invertedDocs.size();
        int left = 0;
        int middle = size / 2;
        int right = size - 1;
        while (middle != left) {
            InvertedDoc middleDoc = invertedDocs.get(middle);
            InvertedDoc middleRightDoc = invertedDocs.get(middle + 1);
            if (middleDoc.getDocId() < invertedDoc.getDocId() && middleRightDoc.getDocId() > invertedDoc.getDocId()) {
                invertedDocs.add(middle, invertedDoc);
            } else {
                if (invertedDoc.getDocId() > invertedDoc.getDocId()) {
                    right = middle;
                    middle = (left + right) / 2;
                } else if (invertedDoc.getDocId() < invertedDoc.getDocId()) {
                    left = middle;
                    middle = (left + right) / 2;
                }
            }
        }
    }

    //文档id
    public static class InvertedDoc {
        long docId;     //文档id
        long docFrequency;   //文档频率

        public InvertedDoc(long docId, long docFrequency) {
            this.docId = docId;
            this.docFrequency = docFrequency;
        }

        public long getDocId() {
            return docId;
        }

        public long getDocFrequency() {
            return docFrequency;
        }

        public void setDocFrequency(long docFrequency) {
            this.docFrequency = docFrequency;
        }
    }
}
