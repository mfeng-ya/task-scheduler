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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wht
 * @date 2018年12月13日
 */
public final class DefaultTaskScheduler implements TaskScheduler {

    public DefaultTaskScheduler(SortedTaskQueue taskQueue, ExecutorService taskExecutor, TaskHandlerLookup taskHandlerLookup,
                                TaskIdGenerator taskIdGenerator) {
        this.taskQueue = Objects.requireNonNull(taskQueue, "null taskQueue");
        this.taskExecutor = Objects.requireNonNull(taskExecutor, "null taskExecutor");
        this.taskHandlerLookup = Objects.requireNonNull(taskHandlerLookup, "null taskHandlerLookup");
        this.taskIdGenerator = Objects.requireNonNull(taskIdGenerator, "null taskIdGenerator");
        this.lock = new ReentrantLock();
        this.con = lock.newCondition();
        main = new Thread(this::start0, "TaskScheduler-main");
        main.setDaemon(true);
        main.start();
    }

    private static final Logger log = LoggerFactory.getLogger(DefaultTaskScheduler.class);

    private final SortedTaskQueue taskQueue;
    private final ExecutorService taskExecutor;
    private final TaskHandlerLookup taskHandlerLookup;
    private final TaskIdGenerator taskIdGenerator;

    private final ReentrantLock lock;
    private final Condition con;

    private final Thread main;

    private void start0() {
        while (true) {
            lock.lock();
            try {
                TaskDefinition taskDefinition = taskQueue.next();
                if (taskDefinition == null) {
                    con.await();
                    continue;
                }
                Instant now = Instant.now();
                Instant activeTime = taskDefinition.getActiveTime();
                if (activeTime.compareTo(now) <= 0) {
                    Object task = taskDefinition.getTask();
                    Runnable runnable = null;
                    if (task instanceof RunnableTask) {
                        runnable = (Runnable) task;
                    } else {
                        TaskHandler taskHandler = taskHandlerLookup.lookup(task);
                        if (taskHandler != null) {
                            runnable = () -> taskHandler.handle(task);
                        } else {
                            log.warn("no handler for task {}", task);
                        }
                    }
                    if (runnable != null) {
                        FutureTask<?> futureTask = new FutureTask<Object>(runnable, null) {
                            @Override
                            protected void done() {
                                //TODO handle exception
                                TaskDefinition next = taskDefinition.next();
                                if (next != null) {
                                    lock.lock();
                                    try {
                                        taskQueue.put(next);
                                        con.signal();
                                    } finally {
                                        lock.unlock();
                                    }
                                }
                            }

                        };
                        taskExecutor.execute(futureTask);
                    }
                } else {
                    taskQueue.put(taskDefinition);
                    long m = activeTime.toEpochMilli() - now.toEpochMilli();
                    con.await(m, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                log.info("TaskScheduler shutdown.");
                break;
            } catch (Exception e) {
                log.error("TaskScheduler has error", e);
                break;
            } finally {
                lock.unlock();
            }
        }
        taskExecutor.shutdown();
    }


    @Override
    public boolean removeTask(String taskId) {
        lock.lock();
        try {
            return taskQueue.remove(taskId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String putTask(Object task, Instant activeTime, RepeatContext repeatContext) {
        lock.lock();
        try {
            String id = taskIdGenerator.generate();
            TaskDefinition taskDefinition = new TaskDefinition(id, task, activeTime, repeatContext);
            taskQueue.put(taskDefinition);
            con.signal();
            return id;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int taskCount() {
        lock.lock();
        try {
            return taskQueue.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void shutdown() {
        if (main.isInterrupted()) {
            return;
        }
        lock.lock();
        try {
            main.interrupt();
        } finally {
            lock.unlock();
        }
    }

    /**
     *
     */
    @Override
    public void clearTask() {
        lock.lock();
        try {
            taskQueue.clear();
        } finally {
            lock.unlock();
        }
    }
}
