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
import java.time.Duration;
import java.time.Period;
import java.util.Objects;

/**
 * @author wht
 */
public final class RepeatContext implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5318929912486229200L;

    private RepeatContext(long times, Duration interval, Period period, boolean fiexedRate) {
        this.times = times;
        this.interval = interval;
        this.period = period;
        this.fiexedRate = fiexedRate;
    }

    public static final RepeatContext ONCE = new RepeatContext(1, null, null, true);

    static RepeatContext create(long times, Duration interval, boolean fiexedRate) {
        Objects.requireNonNull(interval, "null interval");
        return new RepeatContext(times, interval, null, fiexedRate);
    }

    static RepeatContext create(long times, Period period, boolean fiexedRate) {
        Objects.requireNonNull(period, "null period");
        return new RepeatContext(times, null, period, fiexedRate);
    }

    public static RepeatContext fixedRate(Duration interval, long times) {
        return create(times, interval, true);
    }

    public static RepeatContext fixedRate(Period period, long times) {
        return create(times, period, true);
    }

    public static RepeatContext fixedDelay(Duration interval, long times) {
        return create(times, interval, false);
    }

    public static RepeatContext fixedDelay(Period period, long times) {
        return create(times, period, false);
    }

    private final long times;
    private final Duration interval;
    private final Period period;
    private final boolean fiexedRate;

    /**
     * @return times
     */
    public long getTimes() {
        return times;
    }

    public boolean hasNext() {
        return times > 1 || times < 0;
    }

    /**
     * @return interval
     */
    public Duration getInterval() {
        return interval;
    }

    /**
     * @return period
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * @return fiexedRate
     */
    public boolean isFiexedRate() {
        return fiexedRate;
    }

    RepeatContext next() {
        return new RepeatContext(times < 0 ? -1 : times - 1, interval, period, fiexedRate);
    }
}
