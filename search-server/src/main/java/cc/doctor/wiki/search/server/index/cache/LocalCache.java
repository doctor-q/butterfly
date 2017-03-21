package cc.doctor.wiki.search.server.index.cache;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by doctor on 2017/3/21.
 */
public class LocalCache<K, V> implements Cache<K, V> {
    private int segmentCount = 0;
    private Segment<K, V>[] segments;
    private int capability;

    public LocalCache(int capability) {
        this.capability = capability;
        segmentCount = (int) Math.round(2 * Math.log(capability) + 1);
        segments = new Segment[segmentCount];
    }

    @Override
    public V get(K key) {
        int segmentIndex = key.hashCode() % segmentCount;
        Segment<K, V> segment = segments[segmentIndex];
        if (segment == null) {
            return null;
        }
        return segment.get(key);
    }

    @Override
    public V put(K key, V value) {
        int segmentIndex = key.hashCode() % segmentCount;
        Segment<K, V> segment = segments[segmentIndex];
        if (segment == null) {
            segment = new Segment<>(capability / segmentCount);
            segments[segmentIndex] = segment;
        }
        return segment.put(key, value);
    }

    @Override
    public int capability() {
        return capability;
    }

    private static class Segment<K, V> extends ReentrantLock {
        private static final long serialVersionUID = 3188831283091381913L;
        private Map<K, V> datas = new HashMap<>();
        private LinkedList<V> values = new LinkedList<>();
        private int size;

        public Segment(int size) {
            this.size = size;
        }

        public V get(K key) {
            V v = datas.get(key);
            if (v != null) {
                values.remove(v);
                values.addFirst(v);
                return v;
            }
            return null;
        }

        public V put(K key, V value) {
            V remove = null;
            lock();
            V v = datas.get(key);
            if (v != null) {
                values.remove(v);
                values.addFirst(v);
            } else {
                if (datas.size() == size) {
                    remove = values.removeLast();
                    K removeKey = null;
                    for (K k : datas.keySet()) {
                        if (datas.get(k).equals(remove)) {
                            removeKey = k;
                        }
                    }
                    datas.remove(removeKey);
                }
                values.addFirst(value);
                datas.put(key, value);
            }
            datas.put(key, value);
            unlock();
            return remove;
        }
    }
}
