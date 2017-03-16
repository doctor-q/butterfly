package cc.doctor.wiki.search.server.index.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by doctor on 2017/3/8.
 * TODO 加入副本计算
 */
public class DocIdGenerator {
    public static final DocIdGenerator docIdGenerator = new DocIdGenerator();
    private ConcurrentHashMap<String, AtomicLong> indexDocIdMap = new ConcurrentHashMap<>();
    private DocIdGenerator() {}

    public long generateDocId(String index) {
        if (indexDocIdMap.get(index) == null) {
            indexDocIdMap.put(index, new AtomicLong(0));
        }
        return indexDocIdMap.get(index).incrementAndGet();
    }
}
