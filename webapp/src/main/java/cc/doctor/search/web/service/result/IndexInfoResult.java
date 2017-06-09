package cc.doctor.search.web.service.result;

import java.io.Serializable;
import java.util.List;

/**
 * Created by doctor on 2017/5/21.
 */
public class IndexInfoResult implements Serializable {
    private static final long serialVersionUID = -2857787982696882033L;
    private String indexName;
    private List<String> alias;
}
