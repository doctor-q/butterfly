package cc.doctor.wiki.exceptions.rpc;

/**
 * Created by doctor on 2017/3/14.
 */
public class UnSupportOperationException extends RuntimeException {
    private static final long serialVersionUID = -471866996642034365L;

    public UnSupportOperationException(String message) {
        super(message);
    }
}
