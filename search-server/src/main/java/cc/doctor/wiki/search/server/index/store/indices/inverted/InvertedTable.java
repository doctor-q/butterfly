package cc.doctor.wiki.search.server.index.store.indices.inverted;

import cc.doctor.wiki.search.server.index.store.indices.indexer.datastruct.Dichotomy;

import java.io.Serializable;
import java.util.List;

/**
 * Created by doctor on 2017/3/7.
 * 倒排文件中存放的文档信息
 */
public class InvertedTable implements Serializable {
    private static final long serialVersionUID = -8762176665798767763L;
    private String field;
    private WordInfo wordInfo;
    private List<InvertedDoc> invertedDocs;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public WordInfo getWordInfo() {
        return wordInfo;
    }

    public void setWordInfo(WordInfo wordInfo) {
        this.wordInfo = wordInfo;
    }

    public List<InvertedDoc> getInvertedDocs() {
        return invertedDocs;
    }

    public void setInvertedDocs(List<InvertedDoc> invertedDocs) {
        this.invertedDocs = invertedDocs;
    }

    public InvertedDoc getInvertedDoc(Long docId) {
        return Dichotomy.dichotomySearch(invertedDocs, new InvertedDoc(docId, 0));
    }

    public void addInvertedDoc(InvertedDoc invertedDoc) {
        Dichotomy.dichotomyInsert(invertedDocs, invertedDoc);
    }

    //文档id
    public static class InvertedDoc implements Comparable<InvertedDoc>, Serializable {
        private static final long serialVersionUID = -1387682032722468702L;
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

        @Override
        public int compareTo(InvertedDoc invertedDoc) {
            return ((Long)docId).compareTo(invertedDoc.getDocId());
        }
    }
}
