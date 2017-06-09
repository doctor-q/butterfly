package cc.doctor.search.common.utils;

import java.util.*;

/**
 * Created by doctor on 2017/3/16.
 */
public class CollectionUtils {
    public static <T> Collection<T> and(Collection<T> collection1, Collection<T> collection2) {
        for (T o : collection1) {
            if (!collection2.contains(o)) {
                collection1.remove(o);
            }
        }
        return collection1;
    }

    public static <T> Collection<T> or(Collection<T> collection1, Collection<T> collection2) {
        Set<T> set = new LinkedHashSet<>();
        set.addAll(collection1);
        set.addAll(collection2);
        return set;
    }

    public static <T> Collection<T> and(Iterable<T> iterable1, Iterable<T> iterable2) {
        return and(iterableToList(iterable1), iterableToList(iterable2));
    }

    public static <T> List<T> iterableToList(Iterable<T> iterable) {
        List<T> list = new LinkedList<>();
        for (T t : iterable) {
            list.add(t);
        }
        return list;
    }

    public static <T> Collection<T> or(Iterable<T> iterable1, Iterable<T> iterable2) {
        return or(iterableToList(iterable1), iterableToList(iterable2));
    }

    public static <F, T> List<T> transfer(List<F> fromList, Function<F, T> function) {
        List<T> toList = new LinkedList<>();
        for (F from : fromList) {
            toList.add(function.transfer(from));
        }
        return toList;
    }

    public static <T> List<T> list(T ... ts) {
        List<T> list = new LinkedList<>();
        if (ts == null) {
            return list;
        }
        for (T t : ts) {
            list.add(t);
        }
        return list;
    }

    public interface Function<F, T> {
        T transfer(F from);
    }
}
