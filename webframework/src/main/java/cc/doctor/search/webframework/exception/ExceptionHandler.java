package cc.doctor.search.webframework.exception;

/**
 * Created by doctor on 17-6-2.
 */
public interface ExceptionHandler<T> {
    T handleException(Exception e);
}

