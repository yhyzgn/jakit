package com.yhy.jakit.starter.aop.trace;

import com.yhy.jakit.util.cst.Define;
import com.yhy.jakit.util.internal.Trace;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

/**
 * 配合 {@link com.tengyun.saas.starter.component.filter.TraceFilter} 使用
 * <p>
 * Created on 2021-06-04 16:54
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Aspect
@Component
@Slf4j
public class TraceAspect {
    @Value("${spring.application.name:}")
    private String appName;

    /**
     * 切面
     */
    @Pointcut("" +
        "@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
        "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
        "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
        "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
        "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
        "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void tracePoint() {
    }

    /**
     * 环绕增强
     *
     * @param point 切点
     * @return 方法执行结果
     * @throws Throwable 可能出现的异常
     */
    @Around("tracePoint()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == attributes) {
            return null == args || args.length == 0 ? point.proceed() : point.proceed(args);
        }

        Object returned = null == args || args.length == 0 ? point.proceed() : point.proceed(args);

        // 响应是的 Span-Chain 得在这里添加
        HttpServletResponse response = attributes.getResponse();
        if (null != response) {
            String spanChain = Trace.get(Define.SPAN_CHAIN);
            if (StringUtils.hasText(spanChain)) {
                response.setHeader(Define.SPAN_CHAIN, spanChain);
            }
        }
        return returned;
    }
}
