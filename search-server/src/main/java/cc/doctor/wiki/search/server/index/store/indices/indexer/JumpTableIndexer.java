package cc.doctor.wiki.search.server.index.store.indices.indexer;

import cc.doctor.wiki.exceptions.schema.SchemaException;
import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.server.index.store.indices.inverted.WordInfo;
import cc.doctor.wiki.utils.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by doctor on 2017/3/7.
 * 使用跳表做数字查找
 */
public class JumpTableIndexer extends AbstractIndexer {
    //数字跳表,每份索引的每个域包含一个
    private Map<String, ConcurrentSkipListMap<Number, WordInfo>> concurrentSkipListMap = new HashMap<>();

    /**
     * 此处的property必须有且完整
     */
    @Override
    public void insertWordInner(String field, Object value) {
        ConcurrentSkipListMap<Number, WordInfo> skipList = concurrentSkipListMap.get(field);
        if (skipList == null) {
            skipList = new ConcurrentSkipListMap<>();
            concurrentSkipListMap.put(field, skipList);
        }
        Schema.Property wordProperty = schema.getPropertyByName(field);
        if (wordProperty == null || wordProperty.getType() == null) {
            throw new SchemaException("Property error.");
        }
        //时间
        //// TODO: 2017/3/8 加入倒排表信息
        if (wordProperty.getType().equals("date")) {
            String pattern = wordProperty.getPattern();
            if (pattern != null) {
                Date date = DateUtils.parse(field, pattern);
                if (date != null) {
                    skipList.put(date.getTime(), new WordInfo(new WordInfo.InvertedNode(value, 0, 0)));
                }
            }
        } else if (wordProperty.getType().equals("double")) {
            skipList.put(Double.parseDouble(value.toString()), new WordInfo(new WordInfo.InvertedNode(value, 0, 0)));
        } else if (wordProperty.getType().equals("long")) {
            skipList.put(Long.parseLong(value.toString()), new WordInfo(new WordInfo.InvertedNode(value, 0, 0)));
        }
    }

    @Override
    public void deleteWord(Schema schema, String property, Object word) {

    }

    @Override
    public WordInfo getWordInfoInner(String field, Object value) {
        return null;
    }

    @Override
    public List<WordInfo> getWordInfoGreatThanInner(String field, String value) {
        return null;
    }

    @Override
    public List<WordInfo> getWordInfoGreatThanEqualInner(String field, String value) {
        return null;
    }

    @Override
    public List<WordInfo> getWordInfoLessThanInner(String field, String value) {
        return null;
    }

    @Override
    public List<WordInfo> getWordInfoLessThanEqualInner(String field, String value) {
        return null;
    }

    @Override
    public List<WordInfo> getWordInfoPrefixInner(String field, String value) {
        return null;
    }

    @Override
    public List<WordInfo> getWordInfoMatchInner(String field, String value) {
        return null;
    }
}
