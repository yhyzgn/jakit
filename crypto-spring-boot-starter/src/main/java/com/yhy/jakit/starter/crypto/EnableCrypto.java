package com.yhy.jakit.starter.crypto;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 启动自动装配
 * <p>
 * Created on 2022-08-04 15:49
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(CryptoAutoConfiguration.class)
public @interface EnableCrypto {
}
