package cc.doctor.search.webframework.exception;


/**
 * Created by doctor on 17-6-2.
 */
public class SimpleResultExceptionHandler implements ExceptionHandler<String> {

    @Override
    public String handleException(Exception e) {
        return e.getCause().toString();
    }
}
