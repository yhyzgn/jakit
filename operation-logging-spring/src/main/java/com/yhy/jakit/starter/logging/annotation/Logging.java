package com.yhy.jakit.starter.logging.annotation;

import java.lang.annotation.*;

/**
 * 操作日志收集注解
 * <p>
 * Created on 2021-07-08 10:17
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Logging {

    /**
     * 主要操作说明
     *
     * @return 主要操作说明
     */
    String value();
}
