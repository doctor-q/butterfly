package cc.doctor.search.webframework.param;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by doctor on 2017/5/21.
 */
public interface Unpack {
    default void beforeUnpack(HttpServletRequest servletRequest) {}
    default void afterUnpack(HttpServletRequest servletRequest) {}
}
