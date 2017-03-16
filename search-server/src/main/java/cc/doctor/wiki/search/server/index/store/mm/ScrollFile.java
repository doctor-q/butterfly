package cc.doctor.wiki.search.server.index.store.mm;

import cc.doctor.wiki.common.Tuple;
import cc.doctor.wiki.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by doctor on 2017/3/16.
 * 滚动文件
 */
public interface ScrollFile {
    //所有文件
    List<String> files();

    //当前写的状态
    long position();

    //通过position获取文件名
    String getFile(long position);

    //当前文件名
    String current();

    //下一个文件名
    String next();

    <T extends Serializable> long writeSerializable(T serializable);

    /**
     * 读取序列化对象并返回当前位置和序列化对象
     */
    <T extends Serializable> Tuple<Long, T> readSerializable(long position);

    public interface ScrollFileNameStrategy {
        String first();

        String next(String current);
    }

    public class DateScrollFileNameStrategy implements ScrollFileNameStrategy {
        public static final DateScrollFileNameStrategy dateScrollFileNameStrategy = new DateScrollFileNameStrategy();

        private DateScrollFileNameStrategy() {
        }

        @Override
        public String first() {
            return DateUtils.toYMDHMS(new Date());
        }

        @Override
        public String next(String current) {
            return DateUtils.toYMDHMS(new Date());
        }
    }
}
