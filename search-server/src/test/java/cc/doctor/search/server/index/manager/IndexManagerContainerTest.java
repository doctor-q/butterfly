package cc.doctor.search.server.index.manager;

import cc.doctor.search.common.document.Document;
import cc.doctor.search.common.document.Field;
import cc.doctor.search.common.schema.Schema;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by doctor on 2017/3/9.
 */
public class IndexManagerContainerTest {
    AllIndexService allIndexService;
    Schema schema;
    @Before
    public void setUp() {
        schema = new Schema();
        schema.setIndexName("order");
    }
    @Test
    public void createIndex() throws Exception {
        allIndexService.createIndex(schema);
    }

    @Test
    public void dropIndex() throws Exception {
        createIndex();
        allIndexService.dropIndex(schema);
    }

    @Test
    public void createDocument() {
        Document document = new Document();
        allIndexService.createIndex(schema);
        List<Field> fields = new LinkedList<>();
        fields.add(new Field("id", 1));
        fields.add(new Field("name", "chen"));
        fields.add(new Field("date", "2016-10-11 10:20:30"));
        document.setFields(fields);
        allIndexService.dropIndex(schema);
    }

}