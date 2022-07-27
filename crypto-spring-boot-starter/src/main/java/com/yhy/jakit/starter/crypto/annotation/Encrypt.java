package com.yhy.jakit.starter.crypto.annotation;

import java.lang.annotation.*;

/**
 * 加密
 * <p>
 * Created on 2021-05-31 9:35
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Encrypt {
}
