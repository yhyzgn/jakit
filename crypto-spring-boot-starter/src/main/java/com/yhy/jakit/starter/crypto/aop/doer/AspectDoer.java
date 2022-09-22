package com.yhy.jakit.starter.crypto.aop.doer;

import com.yhy.jakit.starter.crypto.aop.model.TypedParam;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 切面执行逻辑接口
 * <p>
 * Created on 2022-09-22 9:33
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public interface AspectDoer {

    Object[] autoDecrypt(HttpServletRequest request, List<TypedParam> paramList, Object handler, Method method, Object[] args);

    Object autoEncrypt(HttpServletRequest request, Object handler, Method method, Object result);
}
