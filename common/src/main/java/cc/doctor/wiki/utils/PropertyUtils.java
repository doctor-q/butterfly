package cc.doctor.wiki.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by doctor on 2017/3/8.
 */
public class PropertyUtils {
    static Map<String, Object> properties = new HashMap<>();

    static {
        loadProperties();
    }

    private static void loadProperties() {

    }

    public static <T> T getProperty(String key, T defaultValue) {
        T value = (T) properties.get(key);
        return value == null ? defaultValue : value;
    }
}
