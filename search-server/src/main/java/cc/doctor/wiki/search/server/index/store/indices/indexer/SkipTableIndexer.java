package cc.doctor.wiki.search.server.index.store.indices.indexer;

import cc.doctor.wiki.common.Range;
import cc.doctor.wiki.exceptions.query.QueryException;
import cc.doctor.wiki.exceptions.schema.SchemaException;
import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.server.index.store.indices.format.Format;
import cc.doctor.wiki.search.server.index.store.indices.inverted.InvertedTable;
import cc.doctor.wiki.search.server.index.store.indices.inverted.WordInfo;
import cc.doctor.wiki.utils.CollectionUtils;
import cc.doctor.wiki.utils.DateUtils;

import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by doctor on 2017/3/7.
 * 使用跳表做数字查找
 */
public class SkipTableIndexer extends AbstractIndexer {
    //数字跳表,每份索引的每个域包含一个
    private Map<String, ConcurrentSkipListMap<Number, WordInfo>> concurrentSkipListMap = new HashMap<>();

    public Map<String, ConcurrentSkipListMap<Number, WordInfo>> getConcurrentSkipListMap() {
        return concurrentSkipListMap;
    }

    /**
     * 此处的property必须有且完整
     */
    @Override
    public void insertWordInner(Collection<Long> docIds, String field, Object value) {
        ConcurrentSkipListMap<Number, WordInfo> skipList = concurrentSkipListMap.get(field);
        if (skipList == null) {
            skipList = new ConcurrentSkipListMap<>();
            concurrentSkipListMap.put(field, skipList);
        }
        Schema.Property wordProperty = schema.getPropertyByName(field);
        if (wordProperty == null || wordProperty.getType() == null) {
            throw new SchemaException("Property error.");
        }
        //逆向更新倒排信息
        InvertedTable invertedTable = new InvertedTable();
        invertedFile.writeInvertedTable(invertedTable);
        skipList.put(checkAndFormatValue(field, value), new WordInfo());
    }

    @Override
    public void deleteWord(Schema schema, String property, Object word) {

    }

    private ConcurrentSkipListMap<Number, WordInfo> checkAndGetSkipListMap(String field) {
        ConcurrentSkipListMap<Number, WordInfo> skipListMap = concurrentSkipListMap.get(field);
        if (skipListMap == null) {
            throw new QueryException("Field not exists.");
        }
        return skipListMap;
    }

    private Number checkAndFormatValue(String field, Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        Schema.Property property = schema.getPropertyByName(field);
        if (property == null) {
            throw new SchemaException("No property.");
        }
        if (property.getType().equals(Format.STRING.getName())) {
            throw new SchemaException("String property not support.");
        }
        if (property.getType().equals(Format.DATE.getName())) {
            Date date = DateUtils.parse(value.toString(), property.getPattern());
            value = date.getTime();
        }
        return (Number) value;
    }

    @Override
    public WordInfo getWordInfoInner(String field, Object value) {
        ConcurrentSkipListMap<Number, WordInfo> skipListMap = checkAndGetSkipListMap(field);
        return skipListMap.get(checkAndFormatValue(field, value));
    }

    @Override
    public List<WordInfo> getWordInfoGreatThanInner(String field, Object value) {
        ConcurrentSkipListMap<Number, WordInfo> skipListMap = checkAndGetSkipListMap(field);
        ConcurrentNavigableMap<Number, WordInfo> tailMap = skipListMap.tailMap(checkAndFormatValue(field, value), false);
        return CollectionUtils.iterableToList(tailMap.values());
    }

    @Override
    public List<WordInfo> getWordInfoGreatThanEqualInner(String field, Object value) {
        ConcurrentSkipListMap<Number, WordInfo> skipListMap = checkAndGetSkipListMap(field);
        ConcurrentNavigableMap<Number, WordInfo> tailMap = skipListMap.tailMap(checkAndFormatValue(field, value), true);
        return CollectionUtils.iterableToList(tailMap.values());
    }

    @Override
    public List<WordInfo> getWordInfoLessThanInner(String field, Object value) {
        ConcurrentSkipListMap<Number, WordInfo> skipListMap = checkAndGetSkipListMap(field);
        ConcurrentNavigableMap<Number, WordInfo> headMap = skipListMap.headMap(checkAndFormatValue(field, value), false);
        return CollectionUtils.iterableToList(headMap.values());
    }

    @Override
    public List<WordInfo> getWordInfoLessThanEqualInner(String field, Object value) {
        ConcurrentSkipListMap<Number, WordInfo> skipListMap = checkAndGetSkipListMap(field);
        ConcurrentNavigableMap<Number, WordInfo> headMap = skipListMap.headMap(checkAndFormatValue(field, value), true);
        return CollectionUtils.iterableToList(headMap.values());
    }

    @Override
    public List<WordInfo> getWordInfoRangeInner(String field, Range range) {
        if (range.getLeft() == null && range.getRight() == null) {
            throw new QueryException("No range.");
        }
        if (range.getLeft() == null) {
            return range.isRightClose() ? getWordInfoLessThanEqualInner(field, range.getRight()) : getWordInfoLessThanInner(field, range.getRight());
        } else if (range.getRight() == null) {
            return range.isLeftClose() ? getWordInfoGreatThanEqualInner(field, range.getRight()) : getWordInfoGreatThanInner(field, range.getRight());
        } else {
            ConcurrentSkipListMap<Number, WordInfo> skipListMap = checkAndGetSkipListMap(field);
            ConcurrentNavigableMap<Number, WordInfo> subMap = skipListMap.subMap(checkAndFormatValue(field, range.getLeft()), range.isLeftClose(),
                    checkAndFormatValue(field, range.getRight()), range.isRightClose());
            return CollectionUtils.iterableToList(subMap.values());
        }
    }

    @Override
    public List<WordInfo> getWordInfoPrefixInner(String field, Object value) {
        throw new QueryException("Number not support prefix query");
    }

    @Override
    public List<WordInfo> getWordInfoMatchInner(String field, Object value) {
        throw new QueryException("Number not support match query");
    }

    @Override
    public void writeLock() {

    }

    @Override
    public void unlock() {

    }
}
