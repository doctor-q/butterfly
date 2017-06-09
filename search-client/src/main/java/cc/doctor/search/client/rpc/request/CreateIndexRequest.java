package cc.doctor.search.client.rpc.request;

import cc.doctor.search.client.index.schema.Schema;

/**
 * Created by doctor on 2017/3/15.
 */
public class CreateIndexRequest extends IndexRequest{
    private static final long serialVersionUID = 4461752767535266119L;
    private String alias;
    private Schema schema;

    public CreateIndexRequest(String indexName) {
        super(indexName);
    }

    public CreateIndexRequest alias(String alias) {
        this.alias = alias;
        return this;
    }

    public CreateIndexRequest schema(Schema schema) {
        this.schema = schema;
        return this;
    }

    CreateIndexRequest newCreateIndexRequest(String indexName) {
        return new CreateIndexRequest(indexName);
    }

    public String getAlias() {
        return alias;
    }

    public Schema getSchema() {
        return schema;
    }
}
