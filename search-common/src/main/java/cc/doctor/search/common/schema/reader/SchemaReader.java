package cc.doctor.search.common.schema.reader;

import cc.doctor.search.common.schema.Schema;

import java.io.InputStream;

/**
 * Created by doctor on 2017/3/3.
 */
public interface SchemaReader {
    Schema read(InputStream inputStream);
}
