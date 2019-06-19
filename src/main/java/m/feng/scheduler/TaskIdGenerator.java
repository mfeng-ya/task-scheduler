package m.feng.scheduler;

import java.util.UUID;

/**
 * @Auther: wht
 * @Date: 2019/6/15 17:02
 * @Description:
 */
public interface TaskIdGenerator {
    String generate();

    static class Default implements TaskIdGenerator {

        @Override
        public String generate() {
            return UUID.randomUUID().toString();
        }
    }
}
