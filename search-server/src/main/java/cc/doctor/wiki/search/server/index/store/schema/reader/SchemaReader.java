package cc.doctor.wiki.search.server.index.store.schema.reader;

import cc.doctor.wiki.search.server.index.store.schema.Schema;

import java.io.InputStream;

/**
 * Created by doctor on 2017/3/3.
 */
public interface SchemaReader {
    Schema read(InputStream inputStream);
}