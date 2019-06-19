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
package m.feng.scheduler.annotation;

import m.feng.scheduler.TaskHandler;

import java.lang.annotation.*;


/**
 * @author wht
 * @date 2018年12月14日
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Task {
    /**
     * @return handler class
     */
    Class<? extends TaskHandler<?>> value();
}
