package cc.doctor.wiki.schedule;

import cc.doctor.wiki.utils.scanner.Scanner;
import cc.doctor.wiki.utils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by doctor on 2017/3/8.
 * 定时任务管理器
 */
public class Scheduler {
    private static final Scheduler scheduler = new Scheduler();
    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
    private Map<String, Task> taskMap = new HashMap<>();

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
    private static String scheduleScanPackage;

    public Scheduler() {
        scheduleScanPackage = PropertyUtils.getProperty("schedule.scan.package", "cc.doctor.wiki.schedule");
//        scanTasks();
    }

    public Map<String, Task> getTaskMap() {
        return taskMap;
    }

    public void scanTasks() {
        Scanner scanner = new Scanner(scheduleScanPackage);
        scanner.doScan();
        ConcurrentHashMap<String, Class> scanClass = scanner.getScanClass();
        for (Class aClass : scanClass.values()) {
            Schedule schedule = (Schedule) aClass.getAnnotation(Schedule.class);
            if (schedule != null) {
                try {
                    Object instance = aClass.newInstance();
                    if (instance instanceof Task) {
                        registerTask((Task)instance, schedule);
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
    }

    public <T> void registerTask(final Task<T> task, Schedule schedule) {
        long duration = schedule.duration();
        String name = schedule.name().equals("") ? task.getClass().getName() : schedule.name();
        taskMap.put(name, task);
        //// TODO: 2017/3/12 add priority
        int priority = schedule.priority();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                T result = task.run();
                task.callback(result);
            }
        }, duration, duration, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        Map<String, Task> taskMap = scheduler.getTaskMap();
        for (String s : taskMap.keySet()) {
            System.out.println(s);
        }
    }
}
