package cc.doctor.search.client.query.document;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by doctor on 2017/3/8.
 */
public class Document implements Serializable {
    private static final long serialVersionUID = 4175591217810356324L;
    private Long id;
    private List<Field> fields = new LinkedList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Document id(Long id) {
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
}
