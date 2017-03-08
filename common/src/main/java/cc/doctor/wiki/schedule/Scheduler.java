package cc.doctor.wiki.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by doctor on 2017/3/8.
 * 定时任务管理器
 */
public class Scheduler {
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
    public <T> void registerTask(final Task<T> task) {
        scheduledExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                T result = task.run();
                task.callback(result);
            }
        });
    }
}
