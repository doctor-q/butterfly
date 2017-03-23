package cc.doctor.wiki.search.server.index.manager;

import java.util.Map;

/**
 * Created by doctor on 2017/3/23.
 */
public class RecoveryServiceContainer {
    private Map<String, IndexManagerInner> indexManagerInnerMap;
    public void doRecovery() {
        for (IndexManagerInner indexManagerInner : indexManagerInnerMap.values()) {
            indexManagerInner.doRecovery();
        }
    }
}
