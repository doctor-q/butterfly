package cc.doctor.wiki.search.server.index.manager;

import cc.doctor.wiki.search.client.index.schema.Schema;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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

    }

    @Test
    public void writeDocumentInner() throws Exception {

    }

    @Test
    public void flush() throws Exception {

    }

}