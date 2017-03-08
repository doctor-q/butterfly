package cc.doctor.wiki.search.server.index.store.indices;

import cc.doctor.wiki.search.server.index.store.schema.Schema;

/**
 * Created by doctor on 2017/3/3.
 */
public abstract class AbstractIndexer {
    //加载schema
    public void loadSchema() {

    }
    //加载索引
    public abstract void loadIndexes(Schema schema);
    public void insertWord(Schema schema, String property,  Object word) {
        if (property == null || word == null) {
            return;
        }
        insertWordInner(schema, property, word);
    }
    //在索引增加一个词
    public abstract void insertWordInner(Schema schema, String property,  Object word);
    //从索引删除一个词
    public abstract void deleteWord(Schema schema, String property, Object word);

}
