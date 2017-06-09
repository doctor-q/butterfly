package cc.doctor.search.common.exceptions.file;

/**
 * Created by doctor on 2017/3/17.
 */
public class MmapFileException extends RuntimeException {
    private static final long serialVersionUID = 4276835622680346410L;

    public MmapFileException(String message) {
        super(message);
    }
}
