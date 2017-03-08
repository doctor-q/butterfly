package cc.doctor.wiki.analysis.filters;

import java.util.Iterator;

/**
 * Created by doctor on 2017/3/7.
 * 空对象过滤器
 */
public class NullFilter implements Filter {
    public String getName() {
        return "null";
    }

    public <T> Iterable<T> filter(final Iterable<T> tokens) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                final Iterator<T> iterator = tokens.iterator();
                return new Iterator<T>() {
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    public T next() {
                        return iterator.next();
                    }

                    public void remove() {

                    }
                };
            }
        };
    }
}
