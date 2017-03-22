package cc.doctor.wiki.exceptions.rpc;

/**
 * Created by doctor on 2017/3/22.
 */
public class TimeoutException extends RuntimeException {
    private static final long serialVersionUID = 2060275287980881894L;

    public TimeoutException(String message) {
        super(message);
    }
}
