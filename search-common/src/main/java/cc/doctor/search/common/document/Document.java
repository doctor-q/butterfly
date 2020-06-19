package cc.doctor.search.common.document;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by doctor on 2017/3/8.
 */
public class Document implements Serializable {
    private static final long serialVersionUID = 4175591217810356324L;
    private String id;
    private List<Field> fields = new LinkedList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Document id(String id) {
        if (id != null) {
            this.id = id;
        }
        return this;
    }

    public Document field(Field field) {
        if (field != null) {
            this.fields.add(field);
        }
        return this;
    }

    public void setIdIfAbsent() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}
