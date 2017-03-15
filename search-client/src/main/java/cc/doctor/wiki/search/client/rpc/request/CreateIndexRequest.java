package cc.doctor.wiki.search.client.rpc.request;

import cc.doctor.wiki.search.client.index.schema.Schema;

/**
 * Created by doctor on 2017/3/15.
 */
public class CreateIndexRequest extends IndexRequest{
    private static final long serialVersionUID = 4461752767535266119L;
    private String alias;
    private Schema schema;

    CreateIndexRequest alias(String alias) {
        this.alias = alias;
        return this;
    }

    CreateIndexRequest schema(Schema schema) {
        this.schema = schema;
        return this;
    }

    CreateIndexRequest newCreateIndexRequest() {
        return new CreateIndexRequest();
    }

    public String getAlias() {
        return alias;
    }

    public Schema getSchema() {
        return schema;
    }
}
