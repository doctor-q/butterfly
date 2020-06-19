package cc.doctor.search.server.index.manager;

import cc.doctor.search.common.schema.Schema;
import cc.doctor.search.common.document.Document;
import cc.doctor.search.common.document.Field;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by doctor on 2017/3/30.
 */
public class IndexManagerInnerTest {
    IndexService indexService;
    Schema schema;

    @Before
    public void setup() {
        schema = new Schema();
        schema.setIndexName("order_info");
        indexService = new IndexService(schema);
    }

    @Test
    public void createIndexInner() throws Exception {
        assert indexService.createIndexInner();
    }

    @Test
    public void dropIndexInner() throws Exception {
        indexService.dropIndexInner();
    }

    @Test
    public void writeDocumentInner() throws Exception {
        createIndexInner();
        Document document = new Document().id("").field(new Field("name", "chen"));
        indexService.insertDocument(document);
        Thread.sleep(1000);
        indexService.flush();
    }

    @Test
    public void flush() throws Exception {

    }

}