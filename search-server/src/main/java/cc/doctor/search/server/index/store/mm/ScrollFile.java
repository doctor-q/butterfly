package cc.doctor.search.server.index.store.mm;

import cc.doctor.search.common.entity.Action;
import cc.doctor.search.common.entity.Tuple;
import cc.doctor.search.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by doctor on 2017/3/16.
 * 滚动文件
 */
public interface ScrollFile {
    String root();
    int scrollSize();
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
    //文件切换的事件
    void onWriteFileCheck(Action action);

    /**
     * 序列化写对象,返回写入的起始位置
     */
    <T extends Serializable> long writeSerializable(T serializable);

    /**
     * 读取序列化对象并返回当前位置和序列化对象,返回null表示文件读取结束
     */
    <T extends Serializable> Tuple<Long, T> readSerializable(long position);

    //写入锁定
    void writeLock();
    //读锁
    void readLock();

    /**
     * 序列化写对象,返回写入的起始位置
     * @param position 写入的位置
     */
    <T extends Serializable> long writeSerializable(long position, T serializable);

    void position(long position);

    public interface ScrollFileNameStrategy {
        String first();

        String next(String current);
    }

    public class AutoIncrementScrollFileNameStrategy implements ScrollFileNameStrategy {
        public static final AutoIncrementScrollFileNameStrategy autoIncrementScrollFileNameStrategy = new AutoIncrementScrollFileNameStrategy();
        private static final int fileNameLength = 5;

        private AutoIncrementScrollFileNameStrategy() {
        }

        @Override
        public String first() {
            return createFileName(0);
        }

        @Override
        public String next(String current) {
            int next = Integer.parseInt(current) + 1;
            return createFileName(next);
        }

        private String createFileName(int index) {
            int fileNumber = index;
            int num = 1;
            while ((fileNumber = fileNumber / 10) != 0) {
                num ++;
            }
            if (num > fileNameLength) {
                return createFileName(0);   //rollback
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < fileNameLength - num; i++) {
                stringBuilder.append("0");
            }
            return stringBuilder.append(String.valueOf(index)).toString();
        }
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
