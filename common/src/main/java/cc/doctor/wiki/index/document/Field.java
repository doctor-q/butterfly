package cc.doctor.wiki.index.document;

/**
 * Created by doctor on 2017/3/8.
 */
public class Field {
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
