package cc.doctor.search.webframework.handler;

import cc.doctor.search.webframework.param.annotation.File;

/**
 * Created by doctor on 17-6-15.
 */
public class FileHandler implements AnnotationHandler<File> {
    @Override
    public Object handler(String parameter, File annotation) {
        return null;
    }
}
