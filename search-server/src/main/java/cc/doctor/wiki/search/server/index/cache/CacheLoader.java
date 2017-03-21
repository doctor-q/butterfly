package cc.doctor.wiki.search.server.index.cache;

/**
 * Created by doctor on 2017/3/21.
 */
public interface CacheLoader<K, V> {
    V load(K key);
}
