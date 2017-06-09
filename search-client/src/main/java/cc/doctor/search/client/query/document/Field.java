package cc.doctor.search.client.query.document;

import java.io.Serializable;

/**
 * Created by doctor on 2017/3/8.
 */
public class Field implements Serializable {
    private static final long serialVersionUID = -4003242243439671574L;
    private String name;
    private Object value;

    public Field(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
