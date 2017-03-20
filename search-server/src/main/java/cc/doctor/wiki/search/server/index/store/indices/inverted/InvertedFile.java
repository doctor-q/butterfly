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
     * @param invertedNode 倒排表索引头信息
     */
    public abstract InvertedTable getInvertedTable(WordInfo.InvertedNode invertedNode);

    /**
     * 写倒排表,非实时刷新,写入内存,当积累到一定程度或定时刷新到文件
     * @param invertedTable 倒排表
     * @return
     */
    public abstract long writeInvertedTable(InvertedTable invertedTable);
    //将倒排表刷盘
    public abstract void flushInvertedTable();


}
