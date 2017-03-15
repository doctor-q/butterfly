package cc.doctor.wiki.search.client.index.schema.typehandlers;

import cc.doctor.wiki.exceptions.schema.TypeHandlerNotFoundException;
import cc.doctor.wiki.search.client.index.schema.reader.SchemaReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by doctor on 2017/3/7.
 */
public class TypeHandlerHolder {
    private static final Logger log = LoggerFactory.getLogger(SchemaReader.class);
    public final static TypeHandlerHolder holder = new TypeHandlerHolder();
    private static Map<String, TypeHandler> nameTypeHandlerMap = new ConcurrentHashMap<String, TypeHandler>();

    private TypeHandlerHolder() {}

    public TypeHandler getTypeHandlerByName(String name) {
        return nameTypeHandlerMap.get(name);
    }

    public TypeHandler getOrCreateTypeHandler(String name, String clazz) {
        TypeHandler typeHandler = holder.getTypeHandlerByName(name);
        if (typeHandler == null) {
            if (clazz != null) {
                try {
                    Class<?> aClass = Class.forName(clazz);
                    holder.putTypeHandler(name, (TypeHandler) aClass.newInstance());
                } catch (Exception e) {
                    log.error("", e);
                    throw new TypeHandlerNotFoundException(e);
                }
            }
        }
        return typeHandler;
    }

    public void putTypeHandler(String name, TypeHandler typeHandler) {
        nameTypeHandlerMap.put(name, typeHandler);
    }
}
