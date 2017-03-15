package cc.doctor.wiki.search.server.index.store.mm.source;

import cc.doctor.wiki.index.document.Document;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by doctor on 2017/3/8.
 */
public abstract class SourceFile {
    private static Map<Long, Integer> idPositionMap = new ConcurrentHashMap<>();    //文档id,对应的Source偏移

    public Integer getPositionById(long id) {
        return idPositionMap.get(id);
    }

    public void setPositionById(long id, int position) {
        idPositionMap.put(id, position);
    }

    /**
     * 写入source
     *
     * @param source
     * @return 写入的位置
     */
    public abstract int appendSource(Source source);

    public static class Source implements Serializable {
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
}
