package m.feng.scheduler;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * @Auther: wht
 * @Date: 2019/6/15 17:02
 * @Description:
 */
public interface TaskIdGenerator extends Supplier<String> {
    default String get() {
        return generate();
    }

    String generate();

    static class Default implements TaskIdGenerator {

        @Override
        public String generate() {
            return UUID.randomUUID().toString();
        }
    }
}
