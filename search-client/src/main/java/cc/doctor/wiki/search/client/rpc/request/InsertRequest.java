package cc.doctor.wiki.search.client.rpc.request;

import cc.doctor.wiki.search.client.query.document.Document;

/**
 * Created by doctor on 2017/3/15.
 */
public class InsertRequest extends IndexRequest {
    private static final long serialVersionUID = -1939249571148691341L;

    private Document document;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
