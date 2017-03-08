package cc.doctor.wiki.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by doctor on 2017/3/8.
 */
public class DateUtils {
    static ThreadLocal<SimpleDateFormat> simpleDateFormatThreadLocal = new ThreadLocal<>();
    public static String format(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = simpleDateFormatThreadLocal.get();
        simpleDateFormat.applyPattern(pattern);
        return simpleDateFormat.format(date);
    }

    public static String toYMDHMS(Date date) {
        return format(date, "yyyyMMddHHmmss");
    }
}
