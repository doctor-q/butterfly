package cc.doctor.wiki.search.server.index.store.indices.indexer;

import cc.doctor.wiki.exceptions.schema.SchemaException;
import cc.doctor.wiki.index.document.Document;
import cc.doctor.wiki.index.document.Field;
import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.server.index.shard.ShardService;
import cc.doctor.wiki.search.server.index.store.indices.format.DateFormat;
import cc.doctor.wiki.search.server.index.store.indices.format.Format;
import cc.doctor.wiki.search.server.index.store.indices.format.FormatProber;
import cc.doctor.wiki.search.server.index.store.indices.inverted.InvertedFile;
import cc.doctor.wiki.search.server.index.store.indices.inverted.WordInfo;

import java.util.LinkedList;
import java.util.List;

import static cc.doctor.wiki.search.server.index.store.indices.format.Format.*;

/**
 * Created by doctor on 2017/3/8.
 * 索引中间者,负责探测数据类型后转交给对应的索引器建立索引,每个索引(分片)拥有一个
 */
public class IndexerMediator {
    private SkipTableIndexer skipTableIndexer;
    private TrieTreeIndexer trieTreeIndexer;
    private InvertedFile invertedFile;
    private ShardService shardService;
    private Schema schema;

    private IndexerMediator() {
        skipTableIndexer = new SkipTableIndexer();
        trieTreeIndexer = new TrieTreeIndexer();
    }

    //为文档建索引
    public void index(Document document) throws Exception {
        List<Field> fields = document.getFields();
        for (Field field : fields) {
            insertWord(document.getId(), field.getName(), field.getValue());
        }
    }

    private Format proberFormatAndSetProperty(Schema.Property property, Object value) {
        Format format = FormatProber.proberFormat(value);
        property.setType(format.getName());
        if (format.equals(DATE)) {
            String pattern = DateFormat.proberPattern(value);
            property.setPattern(pattern);
        }
        return format;
    }

    private boolean insertWord(Long docId, String field, Object value) {
        Format format = getFormat(field);
        if (format == null) {
            format = proberFormatAndSetProperty(schema.getPropertyByName(field), value);
        }
        switch (format) {
            case STRING:
                trieTreeIndexer.insertWord(docId, field, value);
                break;
            case LONG:
            case DOUBLE:
            case DATE:
                skipTableIndexer.insertWord(docId, field, value);
                break;
        }
        return true;
    }

    private Format getFormat(String field) {
        Schema.Property propertyByName = schema.getPropertyByName(field);
        if (propertyByName == null || propertyByName.getType() == null) {
            return null;
        }
        String type = propertyByName.getType();
        Format format = Format.getFormat(type);
        if (format == null) {
            throw new SchemaException("UnSupport field type.");
        }
        return format;
    }

    public Iterable<WordInfo> equalSearch(String field, String value) {
        Format format = getFormat(field);
        WordInfo wordInfo = null;
        switch (format) {
            case STRING:
                wordInfo = trieTreeIndexer.getWordInfoInner(field, value);
                break;
            case LONG:
            case DATE:
            case DOUBLE:
                wordInfo = skipTableIndexer.getWordInfoInner(field, value);
                break;
        }
        if (wordInfo == null) {
            return null;
        }
        List<WordInfo> wordInfos = new LinkedList<>();
        wordInfos.add(wordInfo);
        return wordInfos;
    }

    public Iterable<WordInfo> greatThanSearch(String field, String value) {
        Format format = getFormat(field);
        List<WordInfo> wordInfos = new LinkedList<>();
        switch (format) {
            case STRING:
                throw new SchemaException("Great than predication not support.");
            case LONG:
            case DATE:
            case DOUBLE:
                wordInfos = skipTableIndexer.getWordInfoGreatThanInner(field, value);
                break;
        }
        return wordInfos;
    }

    public Iterable<WordInfo> greatThanEqualSearch(String field, String value) {
        Format format = getFormat(field);
        List<WordInfo> wordInfos = new LinkedList<>();
        switch (format) {
            case STRING:
                throw new SchemaException("Great than predication not support.");
            case LONG:
            case DATE:
            case DOUBLE:
                wordInfos = skipTableIndexer.getWordInfoGreatThanEqualInner(field, value);
                break;
        }
        return wordInfos;
    }

    public Iterable<WordInfo> lessThanSearch(String field, String value) {
        Format format = getFormat(field);
        List<WordInfo> wordInfos = new LinkedList<>();
        switch (format) {
            case STRING:
                throw new SchemaException("Great than predication not support.");
            case LONG:
            case DATE:
            case DOUBLE:
                wordInfos = skipTableIndexer.getWordInfoLessThanInner(field, value);
                break;
        }
        return wordInfos;
    }

    public Iterable<WordInfo> lessThanEqualSearch(String field, String value) {
        Format format = getFormat(field);
        List<WordInfo> wordInfos = new LinkedList<>();
        switch (format) {
            case STRING:
                throw new SchemaException("Great than predication not support.");
            case LONG:
            case DATE:
            case DOUBLE:
                wordInfos = skipTableIndexer.getWordInfoLessThanEqualInner(field, value);
                break;
        }
        return wordInfos;
    }

    public Iterable<WordInfo> prefixSearch(String field, String value) {
        Format format = getFormat(field);
        List<WordInfo> wordInfos = new LinkedList<>();
        switch (format) {
            case STRING:
                wordInfos = trieTreeIndexer.getWordInfoPrefixInner(field, value);
                break;
            case LONG:
            case DATE:
            case DOUBLE:
                throw new SchemaException("Prefix prediction not support.");
        }
        return wordInfos;
    }

    public Iterable<WordInfo> matchSearch(String field, String value) {
        Format format = getFormat(field);
        List<WordInfo> wordInfos = new LinkedList<>();
        switch (format) {
            case STRING:
                wordInfos = trieTreeIndexer.getWordInfoMatchInner(field, value);
                break;
            case LONG:
            case DATE:
            case DOUBLE:
                WordInfo wordInfo = skipTableIndexer.getWordInfoInner(field, value);
                if (wordInfo != null) {
                    wordInfos.add(wordInfo);
                } else {
                    return null;
                }
                break;
        }
        return wordInfos;
    }
}
