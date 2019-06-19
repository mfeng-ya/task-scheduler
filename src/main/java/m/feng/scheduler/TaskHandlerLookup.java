package m.feng.scheduler;

import m.feng.scheduler.annotation.Task;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: wht
 * @Date: 2019/6/15 17:44
 * @Description:
 */
public interface TaskHandlerLookup {

    <T> TaskHandler<T> lookup(T task);

    public static class Default implements TaskHandlerLookup {

        private final ConcurrentHashMap<Class<?>, TaskHandler<?>> handlerMap = new ConcurrentHashMap<>();

        @Override
        public <T> TaskHandler<T> lookup(T task) {
            return (TaskHandler<T>) handlerMap.computeIfAbsent(task.getClass(), this::taskHandler);
        }

        private TaskHandler<?> taskHandler(Class<?> taskClass) {
            Task annotation = taskClass.getAnnotation(Task.class);
            if (annotation == null) {
                throw new RuntimeException(taskClass.getName() + " not with @Task");
            }
            Class<? extends TaskHandler<?>> value = annotation.value();
            try {
                return value.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(value.getName() + "没有无参构造方法");
            }
        }
    }
}
