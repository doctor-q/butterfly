package cc.doctor.wiki.exceptions.query;

/**
 * Created by doctor on 2017/3/14.
 */
public class QueryException extends RuntimeException {
    private static final long serialVersionUID = -7097262994516822175L;

    public QueryException(String message) {
        super(message);
    }

    public QueryException(Throwable cause) {
        super(cause);
    }
}
