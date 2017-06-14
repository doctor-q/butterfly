package cc.doctor.search.webframework.handler;

import cc.doctor.search.common.utils.DateUtils;
import cc.doctor.search.webframework.param.annotation.DataMapper;
import cc.doctor.search.webframework.param.annotation.DateFormat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by doctor on 17-6-9.
 */
public class AnnotationHandlerFactory {
    public static Map<Class, AnnotationHandler> annotationHandlerMap;

    static {
        annotationHandlerMap = new ConcurrentHashMap<>();
        annotationHandlerMap.put(DateFormat.class, new AnnotationHandler<DateFormat>() {

            @Override
            public Object handler(String parameter, DateFormat dateFormat) {
                return DateUtils.parse(parameter, dateFormat.pattern());
            }
        });
        annotationHandlerMap.put(DataMapper.class, new AnnotationHandler<DataMapper>() {
            @Override
            public Object handler(String parameter, DataMapper dataMapper) {
                if (dataMapper.froms().length != dataMapper.tos().length) {
                    return parameter;
                }
                for (int i = 0; i < dataMapper.froms().length; i++) {
                    if (parameter.equals(dataMapper.froms()[i])) {
                        return dataMapper.tos()[i];
                    }
                }
                return parameter;
            }
        });
    }

    public static AnnotationHandler getAnnotationHandler(Class annotation) {
        return annotationHandlerMap.get(annotation);
    }
}
