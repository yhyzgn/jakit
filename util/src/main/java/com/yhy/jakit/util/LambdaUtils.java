package com.yhy.jakit.util;

import com.yhy.jakit.util.lambda.Lambda;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * λ 表达式工具类
 * <p>
 * Created on 2024-02-18 23:25
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public interface LambdaUtils {

    static <T> String getFieldName(Lambda<T, ?> lambda) {
        try {
            Method method = lambda.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda sl = (SerializedLambda) method.invoke(lambda);
            String getter = sl.getImplMethodName();
            return Introspector.decapitalize(getter.replace("get", ""));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
