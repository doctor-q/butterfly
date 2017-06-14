package cc.doctor.search.webframework.handler;

/**
 * Created by doctor on 17-6-9.
 */
public interface AnnotationHandler<A> {
    Object handler(String parameter, A annotation);
}
