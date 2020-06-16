package cc.doctor.search.store.cache;

/**
 * Created by doctor on 2017/3/21.
 */
public interface CacheLoader<K, V> {
    V load(K key);
}
