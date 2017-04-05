package cc.doctor.wiki.search.server.rpc;

/**
 * Created by doctor on 2017/3/20.
 */
public interface Server {
    void start();
    void stop();

    String getHost();

    int getPort();
}
