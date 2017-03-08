package cc.doctor.wiki.schedule;

/**
 * Created by doctor on 2017/3/8.
 */
public interface Task<T> {
    T run();
    void callback(T result);
}
