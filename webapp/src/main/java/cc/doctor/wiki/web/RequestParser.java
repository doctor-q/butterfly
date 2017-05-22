package cc.doctor.wiki.web;

import cc.doctor.wiki.utils.Container;
import cc.doctor.wiki.utils.ReflectUtils;
import cc.doctor.wiki.web.service.param.Unpack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by doctor on 2017/5/21.
 */
public class RequestParser {
    private static final Logger log = LoggerFactory.getLogger(ReflectUtils.class);
    private static final String defaultServicePackage = "cc.doctor.wiki.web.service";
    public static final RequestParser requestParser = new RequestParser();
    private Container container = Container.getContainer("webapp");

    public Object invoke(HttpServletRequest servletRequest, String service, String method) {
        Map<String, String> parametersFromRequest = new HashMap<>();
        try {
            Class<?> serviceClass = Class.forName(defaultServicePackage + "." + service);
            Object serviceInstance = container.getComponent(serviceClass);
            Map<String, Class> methodParamNameTypes = ReflectUtils.getMethodParamNameTypes(method, serviceClass);
            Method serviceClassMethod = serviceClass.getMethod(method, methodParamNameTypes.values().toArray(new Class[0]));
            List<Object> params = new LinkedList<>();
            for (String paramName : methodParamNameTypes.keySet()) {
                Class paramClass = methodParamNameTypes.get(paramName);
                Object instance = paramClass.newInstance();
                if (instance instanceof Unpack) {
                    Map<String, Class> objectAttrNameTypes = ReflectUtils.getObjectAttrNameTypes(paramClass);
                    for (String param : objectAttrNameTypes.keySet()) {
                        Object value = parseParam(servletRequest.getParameter(param), objectAttrNameTypes.get(param));
                        ReflectUtils.set(param, value, instance);
                    }
                } else {
                    instance = parseParam(servletRequest.getParameter(paramName), paramClass);
                }
                params.add(instance);
            }
            return serviceClassMethod.invoke(serviceInstance, params);
        } catch (ReflectiveOperationException e) {
            log.error("", e);
            return null;
        }
    }

    public Object parseParam(String parameter, Class type) {
        Object value = null;
        if (type.equals(Integer.class)) {
            value = Integer.parseInt(parameter);
        } else if (type.equals(Long.class)) {
            value = Long.parseLong(parameter);
        } else if (type.equals(Float.class)) {
            value = Float.parseFloat(parameter);
        } else if (type.equals(Double.class)) {
            value = Double.parseDouble(parameter);
        } else if (type.equals(BigDecimal.class)) {
            value = BigDecimal.valueOf(Double.parseDouble(parameter));
        } else if (type.equals(List.class)) {
            value = Arrays.asList(parameter.split(","));
        } else if (type.equals(Set.class)) {
            try {
                Set<String> set = (Set<String>) type.newInstance();
                set.addAll(Arrays.asList(parameter.split(",")));
                value = set;
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("", e);
            }

        } else if (type.equals(String.class)) {
            value = parameter;
        }
        return value;
    }
}
