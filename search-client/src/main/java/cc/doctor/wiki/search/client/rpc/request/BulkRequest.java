package cc.doctor.wiki.search.client.rpc.request;

import java.io.Serializable;
import java.util.List;

/**
 * Created by doctor on 2017/3/15.
 */
public class BulkRequest<T extends Serializable> extends IndexRequest {
    private static final long serialVersionUID = 3595252177661100774L;
    List<T> bulkData;

    public BulkRequest(String indexName) {
        super(indexName);
    }

    public List<T> getBulkData() {
        return bulkData;
    }

    public void setBulkData(List<T> bulkData) {
        this.bulkData = bulkData;
    }
}
