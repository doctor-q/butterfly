package cc.doctor.wiki.search.server.index.store.indices.indexer;

import cc.doctor.wiki.exceptions.schema.SchemaException;
import cc.doctor.wiki.index.document.Document;
import cc.doctor.wiki.index.document.Field;
import cc.doctor.wiki.search.server.index.store.indices.format.DateFormat;
import cc.doctor.wiki.search.server.index.store.indices.format.Format;
import cc.doctor.wiki.search.server.index.store.indices.format.FormatProber;
import cc.doctor.wiki.search.server.index.store.schema.Schema;

import java.util.List;

/**
 * Created by doctor on 2017/3/8.
 * 索引中间者,负责探测数据类型后转交给对应的索引器建立索引,每个索引(分片)拥有一个
 */
public class IndexerMediator {
    private JumpTableIndexer jumpTableIndexer;
    private TrieTreeIndexer trieTreeIndexer;

    private IndexerMediator() {
        jumpTableIndexer = new JumpTableIndexer();
        trieTreeIndexer = new TrieTreeIndexer();
    }

    //为文档建索引
    public void index(Document document, Schema schema) throws Exception {
        List<Field> fields = document.getFields();
        for (Field field : fields) {
            Schema.Property propertyByName = schema.getPropertyByName(field.getName());
            if (propertyByName != null) {
                if (propertyByName.getType() != null) {
                    Format format = Format.getFormat(propertyByName.getType());
                    if (format != null) {
                        insertWord(schema, format, field);
                    } else {
                        throw new SchemaException("Type error.");
                    }
                } else {
                    Format format = proberFormatAndSetProperty(propertyByName, field.getValue());
                    insertWord(schema, format, field);
                }
            } else {
                Schema.Property property = new Schema.Property(field.getName());
                Format format = proberFormatAndSetProperty(property, field.getValue());
                insertWord(schema, format, field);
            }
        }
    }

    private Format proberFormatAndSetProperty(Schema.Property property, Object value) {
        Format format = FormatProber.proberFormat(value);
        property.setType(format.getName());
        if (format.equals(Format.DATE)) {
            String pattern = DateFormat.proberPattern(value);
            property.setPattern(pattern);
        }
        return format;
    }

    private boolean insertWord(Schema schema,Format format, Field field) {
        switch (format) {
            case STRING:
                trieTreeIndexer.insertWord(schema, field);
                break;
            case LONG:
            case DOUBLE:
            case DATE:
                jumpTableIndexer.insertWord(schema, field);
                break;
        }
        return true;
    }
}
