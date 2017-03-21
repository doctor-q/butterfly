package cc.doctor.wiki.search.server.index.store.indices.inverted;

import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.search.server.index.cache.Cache;
import cc.doctor.wiki.search.server.index.cache.LocalCache;
import cc.doctor.wiki.search.server.index.store.indices.indexer.AbstractIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cc.doctor.wiki.search.server.common.config.Settings.settings;

/**
 * Created by doctor on 2017/3/7.
 * 倒排文件
 */
public abstract class InvertedFile {
    private static final Logger log = LoggerFactory.getLogger(InvertedFile.class);
    public static final int CACHE_INVERTED_TABLE_SIZE = settings.getInt(GlobalConfig.CACHE_INVERTED_TABLE_SIZE);
    protected AbstractIndexer indexer;
    protected Cache<Long, InvertedTable> invertedTableCache = new LocalCache<>(CACHE_INVERTED_TABLE_SIZE);

    /**
     * 获取倒排文件列表
     *
     * @param wordInfo 倒排表索引头信息
     */
    public abstract InvertedTable getInvertedTable(WordInfo wordInfo);

    /**
     * 写倒排表,非实时刷新,写入内存,当积累到一定程度或定时刷新到文件
     * @param invertedTable 倒排表
     * @return
     */
    public abstract void writeInvertedTable(InvertedTable invertedTable);
    //将倒排表刷盘
    public abstract void flushInvertedTable();

    protected void updateWordInfo(String field, WordInfo wordInfo) {
        WordInfo wordInfoInner = indexer.getWordInfoInner(field, wordInfo.getData());
        wordInfoInner.setPosition(wordInfo.getPosition());
    }
}
