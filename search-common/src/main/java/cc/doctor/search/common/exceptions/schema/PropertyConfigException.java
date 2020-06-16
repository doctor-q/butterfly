package cc.doctor.search.common.exceptions.schema;

/**
 * Created by doctor on 2017/3/7.
 */
public class PropertyConfigException extends SchemaException {
    private static final long serialVersionUID = -4875077576834371263L;

    public PropertyConfigException(Exception e) {
        super(e);
    }

    public PropertyConfigException(String message) {
        super(message);
    }
}
