package cc.doctor.search.common.analysis.filters;

/**
 * Created by doctor on 2017/3/7.
 */
public interface Filter {
    String getName();
    <T> Iterable<T> filter(Iterable<T> tokens);
}
