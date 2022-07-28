package com.yhy.jakit.starter.logging.aspect;

import com.yhy.jakit.starter.logging.annotation.Logging;
import com.yhy.jakit.starter.logging.holder.LogHolder;
import com.yhy.jakit.starter.logging.model.Log;
import com.yhy.jakit.starter.logging.provider.UserProvider;
import com.yhy.jakit.starter.logging.writer.LoggingWriter;
import com.yhy.jakit.util.cst.Define;
import com.yhy.jakit.util.system.SystemClock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created on 2021-07-08 10:28
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class LogAspect {
    @Autowired(required = false)
    private UserProvider userProvider;
    @Autowired
    private List<LoggingWriter> writers;

    @Pointcut("@annotation(com.yhy.jakit.starter.logging.annotation.Logging)")
    public void webLog() {
    }

    @Before("webLog()")
    public void before(JoinPoint point) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (null != attributes) {
            request = attributes.getRequest();
        }

        Object target = point.getTarget();
        Signature sig = point.getSignature();
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalStateException("@Logging 注解只能用于方法");
        }
        MethodSignature mSig = (MethodSignature) sig;
        Method method = mSig.getMethod();
        Logging logging = method.getAnnotation(Logging.class);

        Class<?>[] paramTypes = mSig.getParameterTypes();
        String[] paramNames = mSig.getParameterNames();
        StringBuilder sbFunction = new StringBuilder(target.getClass().getCanonicalName()).append(".").append(method.getName()).append("(");
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) {
                sbFunction.append(", ");
            }
            sbFunction.append(paramTypes[i].getName()).append(" ").append(paramNames[i]);
        }
        sbFunction.append(") ").append(method.getReturnType().getName());

        Log lg = LogHolder.newLogging();
        if (null != logging) {
            lg.setDescription(logging.value());
        }
        lg.setTraceId(getTraceId());
        lg.setBegin(SystemClock.now());
        lg.setTransactional(method.isAnnotationPresent(Transactional.class)
            || target.getClass().isAnnotationPresent(Transactional.class)
            || method.isAnnotationPresent(javax.transaction.Transactional.class)
            || target.getClass().isAnnotationPresent(javax.transaction.Transactional.class)
        );
        lg.setUrl(null == request ? null : request.getRequestURL().toString());
        lg.setMethod(null == request ? null : request.getMethod());
        lg.setContentType(null == request ? "" : request.getContentType());
        lg.setFunction(sbFunction.toString());
        lg.setArguments(point.getArgs());

        // 操作人员会话信息
        if (null != userProvider) {
            lg.setUserIdentity(userProvider.identity());
        }

        // 保存
        LogHolder.set(lg);
    }

    @After(value = "webLog()")
    public void after(JoinPoint point) throws Exception {
        Log lg = LogHolder.get();
        if (null != lg) {
            lg.setFinish(SystemClock.now());
            lg.setLatency(lg.getFinish() - lg.getBegin());

            // 输入日志，各业务自己实现
            if (null != writers) {
                for (LoggingWriter writer : writers) {
                    writer.write(lg);
                }
            }

            // 清除当前日志
            LogHolder.remove();
        }
    }


    @AfterThrowing(value = "webLog()", throwing = "th")
    public void afterThrowing(JoinPoint point, Throwable th) {
        Log lg = LogHolder.get();
        if (null != lg) {
            lg.setThrowable(th);
        }
        LogHolder.set(lg);
    }

    private String getTraceId() {
        String traceId = MDC.get(Define.TRACE_ID);
        if (StringUtils.hasText(traceId)) {
            return traceId;
        }
        return "#";
    }
}
