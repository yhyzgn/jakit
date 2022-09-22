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

    /**
     * 自动解密
     *
     * @param request   当前请求对象
     * @param paramList 上层已封装好的参数信息列表
     * @param handler   请求处理器（Controller）
     * @param method    请求处理器方法（method）
     * @param args      参数值列表
     * @return 解密后的参数值列表
     */
    Object[] autoDecrypt(HttpServletRequest request, List<TypedParam> paramList, Object handler, Method method, Object[] args);

    /**
     * 自动加密
     *
     * @param request 当前请求对象
     * @param handler 请求处理器（Controller）
     * @param method  请求处理器方法（method）
     * @param result  请求处理器返回的响应结果
     * @return 加密后的响应数据
     */
    Object autoEncrypt(HttpServletRequest request, Object handler, Method method, Object result);
}
