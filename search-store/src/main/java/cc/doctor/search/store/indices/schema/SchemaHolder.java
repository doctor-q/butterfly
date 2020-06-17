package cc.doctor.search.store.indices.schema;

import cc.doctor.search.common.schema.Schema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by doctor on 2017/3/8.
 */
public class SchemaHolder {
    public static final SchemaHolder schemaHolder = new SchemaHolder();
    private SchemaFile schemaFile;

    private SchemaHolder() {}

    private Map<String, Schema> indexSchemaMap = new ConcurrentHashMap<>();
    public Schema getSchema(String index) {
        return indexSchemaMap.get(index);
    }

    public boolean updateSchemaAndFlush(String index, Schema schema) {
        indexSchemaMap.put(index, schema);
        return schemaFile.writeSchema(schema);
    }
}
