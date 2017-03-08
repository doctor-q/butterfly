package cc.doctor.wiki.search.server.index.store.indices;

import cc.doctor.wiki.search.server.index.store.indices.format.DateFormat;
import cc.doctor.wiki.search.server.index.store.indices.format.FormatProber;
import cc.doctor.wiki.search.server.index.store.indices.inverted.WordInfo;
import cc.doctor.wiki.search.server.index.store.schema.Schema;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by doctor on 2017/3/7.
 * 使用跳表做数字查找
 */
public class JumpTableIndexer extends AbstractIndexer {
    private Map<String, ConcurrentSkipListMap<Number, WordInfo>> concurrentSkipListMap = new HashMap<>();

    @Override
    public void loadIndexes(Schema schema) {

    }

    @Override
    public void insertWordInner(Schema schema, String property, Object word) {
        ConcurrentSkipListMap<Number, WordInfo> skipList = concurrentSkipListMap.get(property);
        if (skipList == null) {
            skipList = new ConcurrentSkipListMap<>();
            concurrentSkipListMap.put(property, skipList);
        }
        Schema.Property wordProperty = schema.getPropertyByName(property);
        if (wordProperty == null) {
            wordProperty = new Schema.Property(property);
            schema.addProperty(wordProperty);
        }
        //时间
        if (wordProperty.getType() != null && wordProperty.getType().equals("date")) {
            String pattern = wordProperty.getPattern();
            if (pattern == null) {
                Long propDate = DateFormat.propeAndTransfer(wordProperty, word);
                if (propDate != null) {
                    skipList.put(propDate, new WordInfo(0, word));
                }
            }
            Long dateLong = DateFormat.transferDate(word.toString());
            if (dateLong != null) {
                skipList.put(dateLong, new WordInfo(0, word));
            }
        }
        //数字
        Number number = FormatProber.toNumber(wordProperty, word);
        if (number != null) {
            skipList.put(number, new WordInfo(0, word));
        }
    }

    @Override
    public void deleteWord(Schema schema, String property, Object word) {

    }

    public static void main(String[] args) {
        JumpTableIndexer jumpTableIndexer = new JumpTableIndexer();
        Schema schema = new Schema();
        jumpTableIndexer.insertWord(schema, "id", 1);
        jumpTableIndexer.insertWord(schema, "name", "cz");
        jumpTableIndexer.insertWord(schema, "id", "2");
        System.out.println(jumpTableIndexer.concurrentSkipListMap);
        System.out.println(schema);
    }
}
