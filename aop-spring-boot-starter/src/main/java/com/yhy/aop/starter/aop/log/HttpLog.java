package com.yhy.aop.starter.aop.log;

import java.lang.annotation.*;

/**
 * 日志信息抓取
 * <p>
 * Created on 2020-05-16 22:07
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface HttpLog {

    /**
     * 日志信息
     *
     * @return 日志信息
     */
    String value() default "";
}
