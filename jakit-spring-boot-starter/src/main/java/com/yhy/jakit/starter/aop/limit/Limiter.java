package com.yhy.jakit.starter.aop.limit;

import java.lang.annotation.*;
import java.time.temporal.ChronoUnit;

/**
 * 分布式限流器
 * <p>
 * 令牌桶方案
 * <p>
 * Created on 2021-04-29 14:32
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Limiter {

    /**
     * 键值，令牌桶标识符
     * <p>
     * 支持参数占位符
     * ${parameter}[${obj.field.field}]
     *
     * @return 键值
     */
    String key();

    /**
     * 令牌桶容量
     * <p>
     * 默认值为 {@link #quota()}
     *
     * @return 桶容量
     */
    long capacity() default 0L;

    /**
     * 规定一定数量令牌的单位时间，同时也是生成一批令牌的单位时间
     * <p>
     * 时间量
     *
     * @return 时间量
     */
    long period();

    /**
     * 规定一定数量令牌的单位时间，同时也是生成一批令牌的单位时间
     * <p>
     * 时间单位
     * <p>
     * 默认值：s
     *
     * @return 时间单位
     */
    ChronoUnit periodUnit() default ChronoUnit.SECONDS;

    /**
     * 单位时间内生成令牌的数量
     *
     * @return 单位时间内生成令牌的数量
     */
    long quota();

    /**
     * 每次需要获取的令牌数
     * <p>
     * 默认值：1
     *
     * @return 每次需要获取的令牌数
     */
    long quantity() default 1L;

    /**
     * 错误异常信息
     *
     * @return 异常信息
     */
    String message() default "操作过于频繁，请稍后再试";
}
