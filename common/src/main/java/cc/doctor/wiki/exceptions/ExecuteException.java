package cc.doctor.wiki.exceptions;

/**
 * Created by doctor on 2017/3/7.
 */
public class ExecuteException extends RuntimeException {
    private static final long serialVersionUID = 5246833784811103445L;

    public ExecuteException(Exception e) {
        super(e);
    }
}
