package cc.doctor.wiki.search.server.cluster.routing;

/**
 * Created by doctor on 2017/3/13.
 * 节点状态,running:正在运行,正常状态;
 * blocked:被阻塞的状态,节点繁忙,不做请求处理;
 * missing:丢失状态,与主节点失去连接,不做请求的处理,丢失一段时间后仍未恢复则变为关闭状态;
 * stop:节点已关闭;
 *
 */
public enum NodeState {
    RUNNING,BLOCKED,MISSING,STOP;
}
