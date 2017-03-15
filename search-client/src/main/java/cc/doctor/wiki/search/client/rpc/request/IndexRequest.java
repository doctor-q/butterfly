package cc.doctor.wiki.search.client.rpc.request;

import java.io.Serializable;

/**
 * Created by doctor on 2017/3/15.
 */
public class IndexRequest implements Serializable {
    private static final long serialVersionUID = -6558157512513500729L;
    private String indexName;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
}
