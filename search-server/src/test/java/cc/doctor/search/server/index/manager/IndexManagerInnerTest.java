package cc.doctor.search.server.index.manager;

import cc.doctor.search.client.index.schema.Schema;
import cc.doctor.search.client.query.document.Document;
import cc.doctor.search.client.query.document.Field;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by doctor on 2017/3/30.
 */
public class IndexManagerInnerTest {
    IndexManagerInner indexManagerInner;
    Schema schema;

    @Before
    public void setup() {
        schema = new Schema();
        schema.setIndexName("order_info");
        indexManagerInner = new IndexManagerInner(schema);
    }

    @Test
    public void createIndexInner() throws Exception {
        assert indexManagerInner.createIndexInner();
    }

    @Test
    public void dropIndexInner() throws Exception {
        indexManagerInner.dropIndexInner();
    }

    @Test
    public void writeDocumentInner() throws Exception {
        createIndexInner();
        Document document = new Document().id(1L).field(new Field("name", "chen"));
        indexManagerInner.insertDocument(document);
        Thread.sleep(1000);
        indexManagerInner.flush();
    }

    @Test
    public void flush() throws Exception {

    }

}