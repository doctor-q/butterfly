package cc.doctor.wiki.search.server.index.manager;

import cc.doctor.wiki.search.server.index.store.schema.Schema;
import org.junit.Before;
import org.junit.Test;

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

}