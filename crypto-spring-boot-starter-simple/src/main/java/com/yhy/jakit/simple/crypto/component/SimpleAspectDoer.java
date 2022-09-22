package com.yhy.jakit.simple.crypto.component;

import com.yhy.jakit.starter.crypto.aop.doer.DefaultAspectDoer;
import com.yhy.jakit.starter.crypto.aop.model.TypedParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created on 2022-09-22 9:54
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
public class SimpleAspectDoer extends DefaultAspectDoer {

    @Override
    public Object[] autoDecrypt(HttpServletRequest request, List<TypedParam> paramList, Object handler, Method method, Object[] args) {
        log.info("自定义的 SimpleAspectDoer.autoDecrypt 执行了");
        return super.autoDecrypt(request, paramList, handler, method, args);
    }

    @Override
    public Object autoEncrypt(HttpServletRequest request, Object handler, Method method, Object result) {
        log.info("自定义的 SimpleAspectDoer.autoEncrypt 执行了");
        return super.autoEncrypt(request, handler, method, result);
    }
}
