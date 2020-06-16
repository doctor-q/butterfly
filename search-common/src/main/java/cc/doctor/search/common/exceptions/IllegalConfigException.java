package cc.doctor.search.common.exceptions;

/**
 * Created by doctor on 2017/3/7.
 */
public class IllegalConfigException extends RuntimeException {
    private static final long serialVersionUID = 5370514812000984190L;

    public IllegalConfigException(Exception e) {
        super(e);
    }

    public IllegalConfigException(String message) {
        super(message);
    }
}
