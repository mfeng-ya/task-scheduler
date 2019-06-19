/**
 * 定风波
 * ———苏轼
 * ————————————————————————————
 * 莫听穿林打叶声，
 * 何妨吟啸且徐行。
 * 竹杖芒鞋轻胜马，谁怕？
 * 一蓑烟雨任平生。
 * 料峭春风吹酒醒，
 * 微冷，山头斜照却相迎。
 * 回首向来萧瑟处，
 * 归去，也无风雨也无晴。
 * ————————————————————————
 **/
package m.feng.scheduler;

import java.io.Serializable;
import java.time.*;
import java.util.Objects;


/**
 * @author wht
 * @date 2018年12月13日
 */
class TaskDefinition implements Serializable, Comparable<TaskDefinition> {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;


    public TaskDefinition(String id, Object task, Instant activeTime, RepeatContext repeatContext) {
        this.id = Objects.requireNonNull(id, "null id");
        this.task = Objects.requireNonNull(task, "null task");
        this.activeTime = Objects.requireNonNull(activeTime, "null activeTime");
        this.repeatContext = Objects.requireNonNull(repeatContext, "null repeatContext");
    }

    private final String id;
    private final Object task;

    private final Instant activeTime;

    private final RepeatContext repeatContext;

    /**
     * @return times
     */
    public long getTimes() {
        return repeatContext.getTimes();
    }

    /**
     * @return repeatable
     */
    public boolean isRepeatable() {
        return repeatContext.hasNext();
    }

    /**
     * @return activeTime
     */
    public Instant getActiveTime() {
        return activeTime;
    }

    /**
     * @return task
     */
    public Object getTask() {
        return task;
    }

    @Override
    public int compareTo(TaskDefinition o) {
        return activeTime.compareTo(o.activeTime);
    }

    public TaskDefinition next() {
        if (!isRepeatable())
            return null;
        final Instant t;
        if (repeatContext.isFiexedRate()) {
            t = activeTime;
        } else {
            t = Instant.now();
        }
        Period period = repeatContext.getPeriod();
        if (period != null) {
            LocalDateTime dateTime = LocalDateTime.ofInstant(t, ZoneId.systemDefault());
            LocalDateTime nextTime = dateTime.plusYears(period.getYears()).plusMonths(period.getMonths())
                    .plusDays(period.getDays());
            Instant activeTime = nextTime.atZone(ZoneId.systemDefault()).toInstant();
            return new TaskDefinition(id, task, activeTime, repeatContext.next());
        }
        Duration interval = repeatContext.getInterval();
        if (interval != null) {
            return new TaskDefinition(id, task, t.plusMillis(interval.toMillis()), repeatContext.next());
        }
        return null;
    }

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

}
