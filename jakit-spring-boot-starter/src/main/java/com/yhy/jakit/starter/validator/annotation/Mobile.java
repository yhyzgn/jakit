package com.yhy.jakit.starter.validator.annotation;

import com.yhy.jakit.starter.validator.constraint.MobileConstraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 校验手机号码
 * <p>
 * Created on 2022-07-26 16:54
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Mobile.List.class)
@Constraint(validatedBy = MobileConstraint.class)
public @interface Mobile {

    /**
     * 错误信息
     *
     * @return 错误信息
     */
    String message() default "{com.yhy.jakit.annotation.validator.starter.Mobile.message}";

    /**
     * groups
     *
     * @return groups
     */
    Class<?>[] groups() default {};

    /**
     * payload
     *
     * @return payload
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 用来匹配的正则表达式
     *
     * @return regexp
     */
    String regexp() default "^1[3456789]\\d{9}$";

    /**
     * Repeatable
     */
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {

        /**
         * Repeatable
         *
         * @return Mobiles
         */
        Mobile[] value();
    }
}
