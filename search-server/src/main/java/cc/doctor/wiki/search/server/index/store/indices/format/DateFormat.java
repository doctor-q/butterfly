package cc.doctor.wiki.search.server.index.store.indices.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by doctor on 2017/3/3.
 */
public class DateFormat {
    private static final Logger log = LoggerFactory.getLogger(DateFormat.class);

    private static ThreadLocal<SimpleDateFormat> dateFormatThreadLocal = new ThreadLocal<SimpleDateFormat>();
    enum DatePattern {
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

        public Long toLong(String dateStr) {
            try {
                dateFormatThreadLocal.get().applyPattern(this.pattern);
                Date date = dateFormatThreadLocal.get().parse(dateStr);
                return date.getTime();
            } catch (ParseException e) {
                log.error("Parse date error.{}", dateStr);
                return null;
            }
        }
    }

    public static Long transferDate(String dateStr) {
        for (DatePattern datePattern : DatePattern.values()) {
            if (datePattern.satisfy(dateStr)) {
                return datePattern.toLong(dateStr);
            }
        }
        return null;
    }
}
