package com.yhy.jakit.starter.crypto.aop;

import com.yhy.jakit.starter.crypto.aop.doer.AspectDoer;
import com.yhy.jakit.starter.crypto.aop.model.TypedParam;
import com.yhy.jakit.starter.crypto.config.CryptoProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
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
    private AspectDoer aspectDoer;

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
        if (!(sig instanceof MethodSignature) || properties.getBasePackages().stream().noneMatch(it -> that.getClass().getPackage().getName().startsWith(it))) {
            return point.proceed();
        }
        MethodSignature mSig = (MethodSignature) sig;
        Method method = mSig.getMethod();
        Object[] args = point.getArgs();

        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Assert.notNull(sra, "请求异常");
        HttpServletRequest request = sra.getRequest();

        // -----------------------------------------------------------------------
        // 1、参数解密
        // -----------------------------------------------------------------------
        List<TypedParam> paramList = getParameters(mSig, args, method);

        log.info("aspectDoer = {}", aspectDoer);

        // 自动解密
        args = aspectDoer.autoDecrypt(request, paramList, that, method, args);

        // -----------------------------------------------------------------------
        // 2、响应结果加密
        // -----------------------------------------------------------------------
        Object returned = null == args || args.length == 0 ? point.proceed() : point.proceed(args);

        // 自动解密
        return aspectDoer.autoEncrypt(request, that, method, returned);
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
}
