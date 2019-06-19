package m.feng.scheduler;

/**
 * @Auther: wht
 * @Date: 2019/6/15 16:34
 * @Description:
 */
public interface TaskHandler<T> {

    void handle(T task);

}
