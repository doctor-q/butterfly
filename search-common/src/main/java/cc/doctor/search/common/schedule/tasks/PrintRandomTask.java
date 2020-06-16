package cc.doctor.search.common.schedule.tasks;

import cc.doctor.search.common.schedule.Schedule;
import cc.doctor.search.common.schedule.Task;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * Created by doctor on 2017/3/12.
 * example task, print random int in callback.
 */
@Schedule(duration = 1000)
@Slf4j
public class PrintRandomTask implements Task {
    private Random random = new Random();

    @Override
    public Object run() {
        return random.nextInt();
    }

    @Override
    public void callback(Object result) {
        log.info(result.toString());
    }
}
