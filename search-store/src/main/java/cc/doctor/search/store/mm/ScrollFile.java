package cc.doctor.search.store.mm;

import cc.doctor.search.common.entity.Action;

import java.util.List;

/**
 * Created by doctor on 2017/3/16.
 * 滚动文件
 */
public interface ScrollFile {
    /**
     * file root directory
     */
    String root();

    /**
     * scroll file size
     */
    int scrollSize();

    //所有文件
    List<String> files();

    //文件切换的事件
    void onWriteFileCheck(Action action);
}
