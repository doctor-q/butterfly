package cc.doctor.search.common.exceptions.schema;

import cc.doctor.search.common.exceptions.IllegalConfigException;

/**
 * Created by doctor on 2017/3/7.
 */
public class SchemaException extends IllegalConfigException {
    private static final long serialVersionUID = -4862134787954092204L;

    public SchemaException(Exception e) {
        super(e);
    }

    public SchemaException(String message) {
        super(message);
    }
}
