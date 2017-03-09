package cc.doctor.wiki.search.server.index.store.indices.indexer;

import cc.doctor.wiki.exceptions.schema.SchemaException;
import cc.doctor.wiki.index.document.Field;
import cc.doctor.wiki.search.server.index.store.indices.inverted.WordInfo;
import cc.doctor.wiki.search.server.index.store.schema.Schema;
import cc.doctor.wiki.utils.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by doctor on 2017/3/7.
 * 使用跳表做数字查找
 */
public class JumpTableIndexer extends AbstractIndexer {
    //数字跳表,每份索引的每个域包含一个
    private Map<String, ConcurrentSkipListMap<Number, WordInfo>> concurrentSkipListMap = new HashMap<>();

    @Override
    public void loadIndexes(Schema schema) {

    }

    /**
     * 此处的property必须有且完整
     */
    @Override
    public void insertWordInner(Schema schema, Field field) {
        ConcurrentSkipListMap<Number, WordInfo> skipList = concurrentSkipListMap.get(field.getName());
        if (skipList == null) {
            skipList = new ConcurrentSkipListMap<>();
            concurrentSkipListMap.put(field.getName(), skipList);
        }
        Schema.Property wordProperty = schema.getPropertyByName(field.getName());
        if (wordProperty == null || wordProperty.getType() == null) {
            throw new SchemaException("Property error.");
        }
        //时间
        //// TODO: 2017/3/8 加入倒排表信息
        if (wordProperty.getType().equals("date")) {
            String pattern = wordProperty.getPattern();
            if (pattern != null) {
                Date date = DateUtils.parse(field.getValue().toString(), pattern);
                if (date != null) {
                    skipList.put(date.getTime(), new WordInfo(new WordInfo.InvertedNode(field.getValue(), 0, 0)));
                }
            }
        } else if (wordProperty.getType().equals("double")) {
            skipList.put(Double.parseDouble(field.getValue().toString()), new WordInfo(new WordInfo.InvertedNode(field.getValue(), 0, 0)));
        } else if (wordProperty.getType().equals("long")) {
            skipList.put(Long.parseLong(field.getValue().toString()), new WordInfo(new WordInfo.InvertedNode(field.getValue(), 0, 0)));
        }
    }

    @Override
    public void deleteWord(Schema schema, String property, Object word) {

    }

    public static void main(String[] args) {
        JumpTableIndexer jumpTableIndexer = new JumpTableIndexer();
        Schema schema = new Schema();
        jumpTableIndexer.insertWord(schema, new Field("id", 1));
        jumpTableIndexer.insertWord(schema, new Field("name", "cz"));
        jumpTableIndexer.insertWord(schema, new Field("id", "2"));
        System.out.println(jumpTableIndexer.concurrentSkipListMap);
        System.out.println(schema);
    }
}
