package cc.doctor.wiki.search.server.index.store.indices.inverted;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by doctor on 2017/3/7.
 * 倒排文件
 */
public abstract class InvertedFile {
    private static final Logger log = LoggerFactory.getLogger(InvertedFile.class);

    /**
     * 获取倒排文件列表
     *
     * @param invertedNode
     */
    public abstract InvertedTable getInvertedTable(WordInfo.InvertedNode invertedNode);

    public abstract long writeInvertedTable(InvertedTable invertedTable);


}
