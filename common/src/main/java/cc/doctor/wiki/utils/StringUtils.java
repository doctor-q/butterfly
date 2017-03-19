package cc.doctor.wiki.utils;

import java.util.UUID;

/**
 * Created by doctor on 2017/3/19.
 */
public class StringUtils {
    public static String base64UUid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
