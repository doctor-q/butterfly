package cc.doctor.wiki.analysis.filters;

/**
 * Created by doctor on 2017/3/7.
 */
public interface Filter {
    String getName();
    <T> Iterable<T> filter(Iterable<T> tokens);
}
