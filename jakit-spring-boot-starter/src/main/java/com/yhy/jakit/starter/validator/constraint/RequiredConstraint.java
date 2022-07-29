package com.yhy.jakit.starter.validator.constraint;

import com.yhy.jakit.starter.validator.annotation.Required;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 必须参数校验器
 * <p>
 * Created on 2022-07-26 17:01
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class RequiredConstraint implements ConstraintValidator<Required, String> {

    private String message;

    @Override
    public void initialize(Required annotation) {
        message = annotation.message();
    }

    @Override
    public boolean isValid(String text, ConstraintValidatorContext context) {
        boolean isValid = StringUtils.hasText(text);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }
        return isValid;
    }
}
