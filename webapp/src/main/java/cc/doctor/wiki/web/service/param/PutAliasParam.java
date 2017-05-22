package cc.doctor.wiki.web.service.param;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by doctor on 2017/5/21.
 */
public class PutAliasParam implements Unpack, Serializable {
    private static final long serialVersionUID = 43598499870509207L;
    private String indexName;
    private List<String> aliases;

    public PutAliasParam(String indexName) {
        this.indexName = indexName;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public PutAliasParam alias(String ... aliases) {
        if (this.aliases == null) {
            this.aliases = new LinkedList<>();
        }
        if (aliases != null) {
            this.aliases.addAll(Arrays.asList(aliases));
        }
        return this;
    }
}
