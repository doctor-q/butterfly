package cc.doctor.wiki.search.server.index.store.indices.indexer;

import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.server.index.store.indices.inverted.InvertedFile;
import cc.doctor.wiki.search.server.index.store.indices.inverted.WordInfo;

import java.util.List;

/**
 * Created by doctor on 2017/3/3.
 */
public abstract class AbstractIndexer {
    protected Schema schema;
    private InvertedFile invertedFile;
    public void insertWord(String field, Object value) {
        if (field == null || value == null) {
            return;
        }
        insertWordInner(field, value);
    }
    //在索引增加一个词
    public abstract void insertWordInner(String field, Object value);
    //从索引删除一个词
    public abstract void deleteWord(Schema schema, String property, Object word);

    //获取索引的倒排信息,等值查询
    public abstract WordInfo getWordInfoInner(String field, Object value);

    //大于查询
    public abstract List<WordInfo> getWordInfoGreatThanInner(String field, String value);

    //大于等于查询
    public abstract List<WordInfo> getWordInfoGreatThanEqualInner(String field, String value);

    //小于查询
    public abstract List<WordInfo> getWordInfoLessThanInner(String field, String value);

    //小于等于查询
    public abstract List<WordInfo> getWordInfoLessThanEqualInner(String field, String value);

    //前缀查询
    public abstract List<WordInfo> getWordInfoPrefixInner(String field, String value);

    //分词查询
    public abstract List<WordInfo> getWordInfoMatchInner(String field, String value);
}
