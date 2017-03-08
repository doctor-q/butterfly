package cc.doctor.wiki.exceptions.schema;

/**
 * Created by doctor on 2017/3/7.
 */
public class TypeHandlerNotFoundException extends SchemaException {

    private static final long serialVersionUID = -88228263861327766L;

    public TypeHandlerNotFoundException(Exception e) {
        super(e);
    }

    public TypeHandlerNotFoundException(String message) {
        super(message);
    }
}
