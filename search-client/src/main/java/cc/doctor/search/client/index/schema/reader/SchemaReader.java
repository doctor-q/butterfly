package cc.doctor.search.client.index.schema.reader;

import cc.doctor.search.client.index.schema.Schema;

import java.io.InputStream;

/**
 * Created by doctor on 2017/3/3.
 */
public interface SchemaReader {
    Schema read(InputStream inputStream);
}
