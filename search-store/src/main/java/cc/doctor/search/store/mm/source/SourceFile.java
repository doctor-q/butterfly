package cc.doctor.search.store.mm.source;

import cc.doctor.search.common.document.Document;
import cc.doctor.search.common.exceptions.index.SourceException;
import cc.doctor.search.store.StoreConfigs;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by doctor on 2017/3/8.
 */
public abstract class SourceFile {
    protected String sourceRoot;
    private static Map<Long, Long> idPositionMap = new ConcurrentHashMap<>();    //文档id,对应的Source偏移

    public Long getPositionById(long id) {
        return idPositionMap.get(id);
    }

    public void setPositionById(long id, long position) {
        idPositionMap.put(id, position);
    }

    public SourceFile() {
        this.sourceRoot = StoreConfigs.SOURCE_PATH_NAME;
    }

    /**
     * 写入source
     *
     * @param source
     * @return 写入的位置
     */
    public abstract long appendSource(Source source);

    public Source getSourceById(long id) {
        Long positionById = getPositionById(id);
        if (positionById != null) {
            return getSource(positionById);
        }
        throw new SourceException("Source not exist.");
    }

    public abstract Source getSource(long position);

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
