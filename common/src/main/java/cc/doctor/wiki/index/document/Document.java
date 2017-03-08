package cc.doctor.wiki.index.document;

import java.io.Serializable;
import java.util.List;

/**
 * Created by doctor on 2017/3/8.
 */
public class Document implements Serializable {
    private static final long serialVersionUID = 4175591217810356324L;
    private Long id;
    private List<Field> fields;

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
}
