package com.yhy.jakit.starter.crypto.aop;

import com.yhy.jakit.starter.crypto.annotation.Decrypt;
import com.yhy.jakit.starter.crypto.annotation.Encrypt;
import com.yhy.jakit.starter.crypto.config.CryptoProperties;
import com.yhy.jakit.starter.crypto.exception.CryptoException;
import com.yhy.jakit.starter.crypto.exception.UnSupportedCryptAlgorithmException;
import com.yhy.jakit.starter.crypto.exec.CryptoExecutor;
import com.yhy.jakit.util.descriptor.GetterSetter;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * AOP
 * <p>
 * Created on 2021-03-30 17:14
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 * <p>
 * 目前仅支持字段级别加密解密，且仅String类型的字段
 */
@Slf4j
@Aspect
@Component
public class CryptoAspect {

    @Autowired
    private CryptoProperties properties;
    @Autowired
    private CryptoExecutor executor;

    @Pointcut("" +
        "@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
        "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
        "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
        "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
        "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
        "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void webCrypt() {
    }

    @Around("webCrypt()")
    public Object before(ProceedingJoinPoint point) throws Throwable {
        Object that = point.getTarget();
        Signature sig = point.getSignature();
        if (!(sig instanceof MethodSignature) || !that.getClass().getPackage().getName().startsWith(properties.getBasePackage())) {
            return point.proceed();
        }
        MethodSignature mSig = (MethodSignature) sig;
        Method method = mSig.getMethod();
        Object[] args = point.getArgs();

        // -----------------------------------------------------------------------
        // 1、参数解密
        // -----------------------------------------------------------------------
        List<TypedParam> paramList = getParameters(mSig, args, method);
        if (!CollectionUtils.isEmpty(paramList)) {
            for (TypedParam param : paramList) {
                // 这些参数当忽略
                if (null == param.value || ServletRequest.class.isAssignableFrom(param.type) || ServletResponse.class.isAssignableFrom(param.type)) {
                    continue;
                }

                // 字段解密
                if (param.type == String.class && param.parameter.isAnnotationPresent(Decrypt.class)) {
                    // String 类型，直接解密
                    String original = (String) param.value;
                    String decrypted = executor.decrypt(original);
                    args[param.index] = decrypted;
                    if (properties.getLogging()) {
                        log.info("[Crypto] The parameter '{}' of method '{}.{}' has been decrypted from '{}' to '{}'", param.name, that.getClass().getCanonicalName(), method.getName(), original, decrypted);
                    }
                } else if (Collection.class.isAssignableFrom(param.type)) {
                    Collection<?> coll = (Collection<?>) param.value;
                    // TODO 支持复杂数据类型
                } else if (!param.type.isPrimitive() && !param.type.getCanonicalName().startsWith("java.lang")) {
                    // 非内置基础类型且非基本包装类型，而是普通自定义类
                    try {
                        doWithFiled(Decrypt.class, new StringBuffer(param.name), param.value, (target, field, value, fieldChain) -> {
                            String original = (String) value;
                            String decrypted = executor.decrypt(original);
                            GetterSetter.invokeSetter(target, field, decrypted);
                            if (properties.getLogging()) {
                                log.info("[Crypto] The parameter '{}' of method '{}.{}' has been decrypted from '{}' to '{}'", fieldChain, that.getClass().getCanonicalName(), method.getName(), original, decrypted);
                            }
                        });
                    } catch (UnSupportedCryptAlgorithmException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new CryptoException("解密失败：" + e.getMessage());
                    }
                } else {
                    // 其他类型...不做任何操作
                }
            }
        }

        // -----------------------------------------------------------------------
        // 2、响应结果加密
        // -----------------------------------------------------------------------
        Object returned = null == args || args.length == 0 ? point.proceed() : point.proceed(args);
        if (null != returned && !returned.getClass().isPrimitive() && (returned.getClass() == Object.class || !returned.getClass().getCanonicalName().startsWith("java.lang"))) {
            try {
                doWithFiled(Encrypt.class, new StringBuffer(returned.getClass().getSimpleName()), returned, (target, field, value, fieldChain) -> {
                    String original = (String) value;
                    String encrypted = executor.encrypt(original);
                    GetterSetter.invokeSetter(target, field, encrypted);
                    if (properties.getLogging()) {
                        log.info("[Crypto] The return value '{}' of method '{}.{}' has been encrypted from '{}' to '{}'", fieldChain, that.getClass().getCanonicalName(), method.getName(), original, encrypted);
                    }
                });
            } catch (UnSupportedCryptAlgorithmException e) {
                throw e;
            } catch (Exception e) {
                throw new CryptoException("加密失败：" + e.getMessage());
            }
        }
        return returned;
    }

    /**
     * 处理字段
     * <p>
     * 递归处理
     *
     * @param annotationClass 字段符合的注解
     * @param target          要处理字段的对象
     * @param fc              字段回调
     */
    private void doWithFiled(Class<? extends Annotation> annotationClass, StringBuffer fieldChain, Object target, FieldCallback fc) throws Exception {
        ReflectionUtils.doWithFields(target.getClass(), field -> {
            StringBuffer sb = new StringBuffer(null == fieldChain ? field.getName() : fieldChain + "." + field.getName());
            try {
                Object value = GetterSetter.invokeGetter(target, field);
                if (null != value) {
                    // 目前仅处理 字符串 类型字段
                    if (field.getType() == String.class) {
                        if (field.isAnnotationPresent(annotationClass)) {
                            fc.doField(target, field, value, sb);
                        }
                    } else if (field.getType() == Object.class || !field.getType().isPrimitive() && !field.getType().getCanonicalName().startsWith("java.lang")) {
                        // 复杂类型递归执行
                        doWithFiled(annotationClass, sb, value, fc);
                    }
                }
            } catch (IllegalStateException e) {
                if (null != e.getMessage() && e.getMessage().startsWith("Not found:")) {
                    // ignore
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, field -> !Modifier.isStatic(field.getModifiers()));
    }

    private List<TypedParam> getParameters(MethodSignature sig, Object[] args, Method method) {
        Class<?>[] paramTypes = sig.getParameterTypes();
        String[] paramNames = sig.getParameterNames();
        Parameter[] parameters = method.getParameters();

        List<TypedParam> params = new ArrayList<>();
        for (int i = 0; i < paramTypes.length && i < paramNames.length && i < args.length && i < parameters.length; i++) {
            params.add(new TypedParam(i, paramTypes[i], paramNames[i], args[i], parameters[i]));
        }
        return params;
    }

    @AllArgsConstructor
    @ToString
    private static class TypedParam {
        private final int index;
        private final Class<?> type;
        private final String name;
        private final Object value;
        private final Parameter parameter;
    }

    @FunctionalInterface
    public interface FieldCallback {

        /**
         * 处理字段
         *
         * @param target     该字段所属的类实例
         * @param field      字段
         * @param value      字段值
         * @param fieldChain 字段名链 xx.yy.zz
         * @throws Exception 可能出现的异常
         */
        void doField(Object target, Field field, Object value, StringBuffer fieldChain) throws Exception;
    }
}
