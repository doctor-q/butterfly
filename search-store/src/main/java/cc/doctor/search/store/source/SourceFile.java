package cc.doctor.search.store.source;

import cc.doctor.search.common.exceptions.index.SourceException;
import cc.doctor.search.store.mm.MmapFile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by doctor on 2017/3/8.
 * source file
 */
public abstract class SourceFile {
    protected String sourceRoot;
    private static Map<Long, Long> idPositionMap = new ConcurrentHashMap<>();    //文档id,对应的Source偏移
    private static MmapFile sourceIndexFile;

    public Long getPositionById(long id) {
        return idPositionMap.get(id);
    }

    public void setPositionById(long id, long position) {
        idPositionMap.put(id, position);
    }

    public SourceFile() {

    }

    public SourceFile(String sourceRoot) {
        this.sourceRoot = sourceRoot;
    }

    /**
     * load source index file
     */
    public void init() {

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


}
