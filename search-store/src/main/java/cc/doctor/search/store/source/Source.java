package cc.doctor.search.store.source;

import cc.doctor.search.common.document.Document;

import java.io.Serializable;

public class Source implements Serializable {
    private static final long serialVersionUID = -3656317010119401027L;
    int size;
    long version;
    Document document;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
