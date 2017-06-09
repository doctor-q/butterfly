package cc.doctor.search.server.index.cache;

/**
 * Created by doctor on 2017/3/21.
 */
public interface Cache<K, V> {
    V get(K key);
    V put(K key, V value);
    int capability();
}
