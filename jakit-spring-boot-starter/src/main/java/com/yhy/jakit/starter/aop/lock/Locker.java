package com.yhy.jakit.starter.aop.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 * <p>
 * Created on 2020-07-04 19:29
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Locker {

    /**
     * 键值
     * <p>
     * 支持参数占位符
     * ${parameter}[${obj.field.field}]
     *
     * @return 键值
     */
    String key();

    /**
     * 过期时间，默认 20
     *
     * @return 过期时间
     */
    long expiry() default 20;

    /**
     * 过期时间单位，默认 s
     *
     * @return 过期时间单位
     */
    TimeUnit expiryUnit() default TimeUnit.SECONDS;

    /**
     * 重试次数，默认 1
     *
     * @return 重试次数
     */
    int retry() default 1;

    /**
     * 重试时间间隔，默认 200ms
     *
     * @return 重试时间间隔，最多等待时间
     */
    long interval() default 200;

    /**
     * 重试时间单位，默认 s
     *
     * @return 重试时间单位
     */
    TimeUnit intervalUnit() default TimeUnit.MILLISECONDS;

    /**
     * 方法执行完后，是否自动解锁
     *
     * @return 是否自动解锁
     */
    boolean autoUnlock() default true;

    /**
     * 错误异常信息
     *
     * @return 异常信息
     */
    String message() default "服务器繁忙，请稍候再试";
}
