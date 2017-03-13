package cc.doctor.wiki.search.server.cluster.node;

/**
 * Created by doctor on 2017/3/13.
 */
public interface LifeCycle {
    void onNodeStart();
    void onNodeStarted();
    void onNodeStop();
}
