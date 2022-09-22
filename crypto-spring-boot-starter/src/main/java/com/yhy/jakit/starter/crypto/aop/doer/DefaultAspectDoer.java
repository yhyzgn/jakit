package com.yhy.jakit.starter.crypto.aop.doer;

import com.yhy.jakit.starter.crypto.annotation.Decrypt;
import com.yhy.jakit.starter.crypto.annotation.Encrypt;
import com.yhy.jakit.starter.crypto.aop.model.TypedParam;
import com.yhy.jakit.starter.crypto.config.CryptoProperties;
import com.yhy.jakit.starter.crypto.exception.CryptoException;
import com.yhy.jakit.starter.crypto.exception.UnSupportedCryptAlgorithmException;
import com.yhy.jakit.starter.crypto.exec.CryptoExecutor;
import com.yhy.jakit.util.ReflectionUtils;
import com.yhy.jakit.util.descriptor.GetterSetter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * 默认实现
 * <p>
 * Created on 2022-09-22 9:44
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class DefaultAspectDoer implements AspectDoer {
    @Autowired
    private CryptoProperties properties;
    @Autowired
    private CryptoExecutor executor;

    @Override
    public Object[] autoDecrypt(HttpServletRequest request, List<TypedParam> paramList, Object handler, Method method, Object[] args) {
        if (!CollectionUtils.isEmpty(paramList)) {
            for (TypedParam param : paramList) {
                // 这些参数当忽略
                if (null == param.getValue() || ServletRequest.class.isAssignableFrom(param.getType()) || ServletResponse.class.isAssignableFrom(param.getType())) {
                    continue;
                }

                // 字段解密
                if (param.getType() == String.class && param.getParameter().isAnnotationPresent(Decrypt.class)) {
                    // String 类型，直接解密
                    String original = (String) param.getValue();
                    String decrypted = executor.decrypt(original);
                    args[param.getIndex()] = decrypted;
                    if (properties.getLogging()) {
                        log.info("[Crypto] The parameter '{}' of method '{}.{}' has been decrypted from '{}' to '{}'", param.getName(), handler.getClass().getCanonicalName(), method.getName(), original, decrypted);
                    }
                } else if (Collection.class.isAssignableFrom(param.getType())) {
                    Collection<?> coll = (Collection<?>) param.getValue();
                    // TODO 支持复杂数据类型
                } else if (!param.getType().isPrimitive() && !param.getType().getCanonicalName().startsWith("java.lang")) {
                    // 非内置基础类型且非基本包装类型，而是普通自定义类
                    try {
                        ReflectionUtils.doFields(Decrypt.class, new StringBuffer(param.getName()), param.getValue(), (target, field, value, fieldChain) -> {
                            String original = (String) value;
                            String decrypted = executor.decrypt(original);
                            GetterSetter.invokeSetter(target, field, decrypted);
                            if (properties.getLogging()) {
                                log.info("[Crypto] The parameter '{}' of method '{}.{}' has been decrypted from '{}' to '{}'", fieldChain, handler.getClass().getCanonicalName(), method.getName(), original, decrypted);
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
        return args;
    }

    @Override
    public Object autoEncrypt(HttpServletRequest request, Object handler, Method method, Object result) {
        if (null != result && !result.getClass().isPrimitive() && (result.getClass() == Object.class || !result.getClass().getCanonicalName().startsWith("java.lang"))) {
            try {
                ReflectionUtils.doFields(Encrypt.class, new StringBuffer(result.getClass().getSimpleName()), result, (target, field, value, fieldChain) -> {
                    String original = (String) value;
                    String encrypted = executor.encrypt(original);
                    GetterSetter.invokeSetter(target, field, encrypted);
                    if (properties.getLogging()) {
                        log.info("[Crypto] The return value '{}' of method '{}.{}' has been encrypted from '{}' to '{}'", fieldChain, handler.getClass().getCanonicalName(), method.getName(), original, encrypted);
                    }
                });
            } catch (UnSupportedCryptAlgorithmException e) {
                throw e;
            } catch (Exception e) {
                throw new CryptoException("加密失败：" + e.getMessage());
            }
        }
        return result;
    }
}
