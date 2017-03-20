package cc.doctor.wiki.search.server.index.store.indices.indexer;

import cc.doctor.wiki.common.Range;
import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.server.index.store.indices.inverted.InvertedFile;
import cc.doctor.wiki.search.server.index.store.indices.inverted.InvertedTable;
import cc.doctor.wiki.search.server.index.store.indices.inverted.WordInfo;

import java.util.List;

/**
 * Created by doctor on 2017/3/3.
 */
public abstract class AbstractIndexer {
    protected Schema schema;
    protected InvertedFile invertedFile;
    public void insertWord(Long docId, String field, Object value) {
        if (field == null || value == null) {
            return;
        }
        WordInfo wordInfoInner = getWordInfoInner(field, value);
        if (wordInfoInner == null) {    //词不存在,则创建新的倒排空间
            insertWordInner(docId, field, value);
        } else {    //词已经存在,更新原来的倒排空间
            updateInvertedDocs(docId, wordInfoInner);
        }
    }

    protected void updateInvertedDocs(Long docId, WordInfo wordInfo) {
        InvertedTable invertedTable = invertedFile.getInvertedTable(wordInfo.getInvertedNode());
        InvertedTable.InvertedDoc invertedDoc = invertedTable.getInvertedDoc(docId);
        if (invertedDoc != null) {
            invertedDoc.setDocFrequency(invertedDoc.getDocFrequency() + 1);
        } else {
            invertedTable.addInvertedDoc(new InvertedTable.InvertedDoc(docId, 1));
        }
    }

    //在索引增加一个词
    public abstract void insertWordInner(Long docId, String field, Object value);
    //从索引删除一个词
    public abstract void deleteWord(Schema schema, String property, Object word);

    //获取索引的倒排信息,等值查询
    public abstract WordInfo getWordInfoInner(String field, Object value);

    //大于查询
    public abstract List<WordInfo> getWordInfoGreatThanInner(String field, Object value);

    //大于等于查询
    public abstract List<WordInfo> getWordInfoGreatThanEqualInner(String field, Object value);

    //小于查询
    public abstract List<WordInfo> getWordInfoLessThanInner(String field, Object value);

    //小于等于查询
    public abstract List<WordInfo> getWordInfoLessThanEqualInner(String field, Object value);

    public abstract List<WordInfo> getWordInfoRangeInner(String field, Range range);

    //前缀查询
    public abstract List<WordInfo> getWordInfoPrefixInner(String field, Object value);

    //分词查询
    public abstract List<WordInfo> getWordInfoMatchInner(String field, Object value);
}
