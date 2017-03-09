package cc.doctor.wiki.exceptions.index;

import cc.doctor.wiki.exceptions.ExecuteException;

/**
 * Created by doctor on 2017/3/9.
 */
public class NoCheckPointException extends ExecuteException {
    public NoCheckPointException(Exception e) {
        super(e);
    }

    public NoCheckPointException(String message) {
        super(message);
    }
}
