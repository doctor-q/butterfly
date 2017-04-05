package cc.doctor.wiki.search.server.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by doctor on 2017/3/23.
 */
public class Container {
    public static final Container container = new Container();
    public Map<String, Object> components = new ConcurrentHashMap<>();

    public void addComponent(Object object) {
        if (object != null) {
            components.put(object.getClass().getName(), object);
        }
    }

    public <T> T getComponent(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        String clazzName = clazz.getName();
        return (T) getComponent(clazzName);
    }

    public Object getComponent(String name) {
        if (name == null) {
            return null;
        }
        return components.get(name);
    }
}
