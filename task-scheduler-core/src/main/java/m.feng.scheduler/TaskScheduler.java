package m.feng.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;

/**
 * @Auther: wht
 * @Date: 2019/6/15 16:36
 * @Description:
 */
public interface TaskScheduler {


    /**
     * 停止服务
     */
    void shutdown();

    void clearTask();

    /**
     * 移除任务
     *
     * @param taskId 任务ID
     * @return
     */
    boolean removeTask(String taskId);

    /**
     * 添加单次任务
     */
    default String putOnceTask(Object task, Instant activeTime) {
        return putTask(task, activeTime, RepeatContext.ONCE);
    }

    /**
     * 添加固定频次任务
     */
    default String putFixedRateTask(Object task, Instant activeTime, Duration interval) {
        return putTask(task, activeTime, RepeatContext.fixedRate(interval, -1));
    }

    /**
     * 添加固定频次任务
     */
    default String putFixedRateTask(Object task, Instant activeTime, Period period) {
        return putTask(task, activeTime, RepeatContext.fixedRate(period, -1));
    }

    /**
     * 添加固定延迟任务
     *
     * @param task
     * @param activeTime
     * @param period
     * @return
     */
    default String putFiexdDelayTask(Object task, Instant activeTime, Period period) {
        return putTask(task, activeTime, RepeatContext.fixedDelay(period, -1));
    }

    /**
     * 添加固定延迟任务
     *
     * @param task
     * @param activeTime
     * @param interval
     * @return
     */
    default String putFiexdDelayTask(Object task, Instant activeTime, Duration interval) {
        return putTask(task, activeTime, RepeatContext.fixedDelay(interval, -1));
    }

    String putTask(Object task, Instant activeTime, RepeatContext repeatContext);

    int taskCount();

}
