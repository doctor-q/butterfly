package cc.doctor.wiki.exceptions.index;

import cc.doctor.wiki.exceptions.ExecuteException;

/**
 * Created by doctor on 2017/3/7.
 */
public class IndexException extends ExecuteException {
    private static final long serialVersionUID = 4345929350351867338L;

    public IndexException(Exception e) {
        super(e);
    }
}
