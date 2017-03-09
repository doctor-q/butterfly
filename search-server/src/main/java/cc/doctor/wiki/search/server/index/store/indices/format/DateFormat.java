package cc.doctor.wiki.search.server.index.store.indices.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by doctor on 2017/3/3.
 */
public class DateFormat {
    private static final Logger log = LoggerFactory.getLogger(DateFormat.class);

    private static ThreadLocal<SimpleDateFormat> dateFormatThreadLocal = new ThreadLocal<SimpleDateFormat>();

    public static Date proberDate(String word) {
        for (DatePattern datePattern : DatePattern.values()) {
            if (datePattern.satisfy(word)) {
                try {
                    dateFormatThreadLocal.get().applyPattern(datePattern.pattern);
                    return dateFormatThreadLocal.get().parse(word);
                } catch (Exception ignore) {}
            }
        }
        return null;
    }

    public static String proberPattern(Object value) {
        for (DatePattern datePattern : DatePattern.values()) {
            if (datePattern.satisfy(value.toString())) {
                return datePattern.pattern;
            }
        }
        return null;
    }

    private enum DatePattern {
        DATETIME("yyyy-MM-dd HH:mm:ss") {
            @Override
            public boolean satisfy(String dateStr) {
                return dateStr.matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}");
            }
        },
        DATE("yyyy-MM-dd") {
            @Override
            public boolean satisfy(String dateStr) {
                return dateStr.matches("\\d{4}-\\d{2}-\\d{2}");
            }
        },
        TIME("HH:mm:ss") {
            @Override
            public boolean satisfy(String dateStr) {
                return dateStr.matches("\\d{2}:\\d{2}:\\d{2}");
            }
        };
        private String pattern;

        DatePattern(final String pattern) {
            this.pattern = pattern;
        }

        public boolean satisfy(String dateStr) {
            return false;
        }
    }
}
