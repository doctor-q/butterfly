package cc.doctor.wiki.search.client.rpc.request;

/**
 * Created by doctor on 2017/3/15.
 */
public class DeleteRequest extends IndexRequest {
    private static final long serialVersionUID = 3798860012217033297L;

    private Long docId;

    public DeleteRequest(String indexName) {
        super(indexName);
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }
}
