package com.yhy.jakit.starter.validator.constraint;

import com.yhy.jakit.starter.validator.annotation.Mobile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * 手机号码校验器
 * <p>
 * Created on 2022-07-26 17:00
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class MobileConstraint implements ConstraintValidator<Mobile, String> {

    private Pattern pattern;
    private String message;

    @Override
    public void initialize(Mobile annotation) {
        pattern = Pattern.compile(annotation.regexp());
        message = annotation.message();
    }

    @Override
    public boolean isValid(String text, ConstraintValidatorContext context) {
        boolean isValid = null != text && pattern.matcher(text).matches();
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }
        return isValid;
    }
}
