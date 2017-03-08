package cc.doctor.wiki.search.server.index.store.indices.task;

import cc.doctor.wiki.annotation.Schedule;
import cc.doctor.wiki.schedule.Task;

/**
 * Created by doctor on 2017/3/8.
 * 合并倒排链定时任务
 */
@Schedule(duration = 20 * 20 * 20, name = "mergeInvertedTableTask")
public class MergeInvertedTableTask implements Task<MergeInvertedTableTask.MergeInvertedTableTaskResult> {

    public MergeInvertedTableTaskResult run() {
        return null;
    }

    @Override
    public void callback(MergeInvertedTableTask.MergeInvertedTableTaskResult mergeInvertedTableTaskResult) {

    }

    class MergeInvertedTableTaskResult {

    }
}
