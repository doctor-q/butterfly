package cc.doctor.search.client.rpc.result;

import java.util.List;
import java.util.Map;

/**
 * Created by doctor on 2017/3/14.
 */
public class SearchResult extends ShardRpcResult {
    private static final long serialVersionUID = -4277648281913246713L;

    private String query;
    private List<SourceData> sourceDataList;

    private class SourceData {
        private String index;
        private long docId;
        private Map<String, Object> source;
    }
}
