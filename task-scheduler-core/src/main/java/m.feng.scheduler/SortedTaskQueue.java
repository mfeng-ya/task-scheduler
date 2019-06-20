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

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Supplier;

/**
 * 持久化需自己实现 比如使用redis
 *
 * @author wht
 * @date 2018年12月13日
 */
public interface SortedTaskQueue extends Supplier<TaskDefinition> {

    /**
     * <p>
     * Title: put
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param task
     */
    void put(TaskDefinition task);

    boolean remove(String taskId);

    void clear();

    default TaskDefinition get() {
        return next();
    }

    /**
     * <p>
     * Title: next
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @return
     */
    TaskDefinition next();

    int size();

    boolean equals(Object obj);

    public static class Default implements SortedTaskQueue {
        private PriorityQueue<TaskDefinition> queue = new PriorityQueue<>();
        private Map<String, TaskDefinition> map = new HashMap<>();

        @Override
        public void put(TaskDefinition task) {
            queue.offer(task);
            map.put(task.getId(), task);
        }

        @Override
        public TaskDefinition next() {
            TaskDefinition task = queue.poll();
            if (task != null) {
                map.remove(task.getId());
            }
            return task;
        }

        @Override
        public int size() {
            return queue.size();
        }

        @Override
        public boolean remove(String taskId) {
            TaskDefinition taskDefinition = map.get(taskId);
            if (taskDefinition != null) {
                queue.remove(taskDefinition);
                map.remove(taskId);
                return true;
            }
            return false;
        }

        /**
         *
         */
        @Override
        public void clear() {
            queue.clear();
            map.clear();
        }
    }
}
