package cc.doctor.search.client.rpc.request;

import cc.doctor.search.client.query.QueryBuilder;

/**
 * Created by doctor on 2017/3/15.
 */
public class QueryRequest extends IndexRequest {
    private static final long serialVersionUID = 6223481409973167445L;

    private QueryBuilder queryBuilder;

    public QueryRequest(String indexName) {
        super(indexName);
    }

    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }
}
