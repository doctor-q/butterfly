package cc.doctor.wiki.exceptions.cluster;

/**
 * Created by doctor on 2017/3/20.
 */
public class VoteException extends RuntimeException {
    private static final long serialVersionUID = -6928242222790987386L;

    public VoteException(String message) {
        super(message);
    }
}
