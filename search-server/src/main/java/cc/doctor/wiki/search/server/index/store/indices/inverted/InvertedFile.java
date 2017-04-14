package cc.doctor.wiki.search.server.index.store.indices.inverted;

import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.search.server.index.cache.Cache;
import cc.doctor.wiki.search.server.index.cache.LocalCache;
import cc.doctor.wiki.search.server.index.shard.ShardService;
import cc.doctor.wiki.search.server.index.store.indices.indexer.AbstractIndexer;
import cc.doctor.wiki.search.server.index.store.indices.indexer.IndexerMediator;
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
    protected IndexerMediator indexerMediator;
    protected Cache<Long, InvertedTable> invertedTableCache = new LocalCache<>(CACHE_INVERTED_TABLE_SIZE);

    public InvertedFile(IndexerMediator indexerMediator) {
        this.indexerMediator = indexerMediator;
    }

    /**
     * 获取倒排文件列表
     *
     * @param wordInfo 倒排表索引头信息
     */
    public abstract InvertedTable getInvertedTable(WordInfo wordInfo);

    /**
     * 写倒排表,非实时刷新,写入内存,当积累到一定程度或定时刷新到文件
     * @param invertedTable 倒排表
     */
    public abstract void writeInvertedTable(InvertedTable invertedTable);
    //将倒排表刷盘
    public abstract void flushInvertedTable();

    /**
     * 反向更新倒排索引信息
     * @param field 字段
     * @param wordInfo 倒排索引信息
     */
    protected void updateWordInfo(String field, WordInfo wordInfo) {
        if (field == null || wordInfo == null || wordInfo.getData() == null) {
            return;
        }
        Iterable<WordInfo> wordInfos = indexerMediator.equalSearch(field, wordInfo.getData().toString());
        if (wordInfos != null && wordInfos.iterator().hasNext()) {
            wordInfos.iterator().next().setPosition(wordInfo.getPosition());
        }
    }
}
