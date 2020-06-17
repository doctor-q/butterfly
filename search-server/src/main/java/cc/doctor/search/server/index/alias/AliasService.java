package cc.doctor.search.server.index.alias;

import java.util.List;

public interface AliasService {
    void addAlias(String index, String alias);

    void removeAlias(String index, String alias);

    List<String> getIndexOfAlias(String alias);
}
