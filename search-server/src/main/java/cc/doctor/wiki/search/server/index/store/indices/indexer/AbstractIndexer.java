package cc.doctor.wiki.search.server.index.store.indices.indexer;

import cc.doctor.wiki.index.document.Field;
import cc.doctor.wiki.search.client.index.schema.Schema;

/**
 * Created by doctor on 2017/3/3.
 */
public abstract class AbstractIndexer {
    //加载schema
    public void loadSchema() {

    }
    //加载索引
    public abstract void loadIndexes(Schema schema);
    public void insertWord(Schema schema, Field field) {
        if (field.getName() == null || field.getValue() == null) {
            return;
        }
        insertWordInner(schema, field);
    }
    //在索引增加一个词
    public abstract void insertWordInner(Schema schema, Field field);
    //从索引删除一个词
    public abstract void deleteWord(Schema schema, String property, Object word);

}
