package cc.doctor.wiki.search.server.index.manager;

import cc.doctor.wiki.index.document.Document;
import cc.doctor.wiki.index.document.Field;
import cc.doctor.wiki.search.server.index.store.schema.Schema;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static cc.doctor.wiki.search.server.index.manager.IndexManagerContainer.indexManagerContainer;

/**
 * Created by doctor on 2017/3/9.
 */
public class IndexManagerContainerTest {
    Schema schema;
    @Before
    public void setUp() {
        schema = new Schema();
        schema.setIndexName("order");
    }
    @Test
    public void createIndex() throws Exception {
        indexManagerContainer.createIndex(schema);
    }

    @Test
    public void dropIndex() throws Exception {
        createIndex();
        indexManagerContainer.dropIndex(schema);
    }

    @Test
    public void createDocument() {
        Document document = new Document();
        indexManagerContainer.createIndex(schema);
        List<Field> fields = new LinkedList<>();
        fields.add(new Field("id", 1));
        fields.add(new Field("name", "chen"));
        fields.add(new Field("date", "2016-10-11 10:20:30"));
        document.setFields(fields);
        indexManagerContainer.writeDocument(schema, document);
        indexManagerContainer.dropIndex(schema);
    }

}