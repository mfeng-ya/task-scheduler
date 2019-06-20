package m.feng.scheduler;

import java.util.function.Consumer;

/**
 * @Auther: wht
 * @Date: 2019/6/15 16:34
 * @Description:
 */
public interface TaskHandler<T> extends Consumer<T> {

    default void accept(T t) {
        handle(t);
    }

    void handle(T task);

}
