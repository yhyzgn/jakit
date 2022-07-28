package com.yhy.jakit.starter.aop.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yhy.jakit.starter.wrapper.RequestWrapper;
import com.yhy.jakit.util.cst.Define;
import com.yhy.jakit.util.internal.Maps;
import com.yhy.jakit.util.system.SystemClock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 统一控制台日志处理
 * <p>
 * Created on 2020-05-16 21:43
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Aspect
@Component
@Slf4j
public class HttpLogAspect {
    private final static SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    private final static SimpleDateFormat FORMAT_DATE_WEEK = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS E", Locale.getDefault());
    private final static String header = "======================================================== {} Start ========================================================";
    private final static String footer = "========================================================= {} End =========================================================";
    private final static String lineSeparator = System.lineSeparator();
    private final static String space = " ";
    private final static ThreadLocal<Long> startAt = new InheritableThreadLocal<>();
    private final static ThreadLocal<StringBuffer> message = new InheritableThreadLocal<>();
    private final static ThreadLocal<ServletRequestAttributes> attributesTL = new InheritableThreadLocal<>();

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 请求日志切面
     */
    @Pointcut("execution(public * *..*.*(..)) && (@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController))")
    public void webLog() {
    }

    /**
     * 统一异常处理器日志切面
     */
    @Pointcut("execution(public * *..*.*(..)) && (@within(org.springframework.web.bind.annotation.ControllerAdvice) || @within(org.springframework.web.bind.annotation.RestControllerAdvice))")
    public void expLog() {
    }

    /**
     * 前置增强
     *
     * @param point 切点
     */
    @Before("webLog()")
    public void around(JoinPoint point) {
        String realHeader = header.replace("{}", getTraceId());
        String banner = String.join("", Collections.nCopies(realHeader.length(), "-"));

        StringBuffer sb = new StringBuffer(lineSeparator).append(realHeader).append(lineSeparator);
        startAt.set(SystemClock.now());

        sb.append(space).append(FORMAT_DATE_WEEK.format(new Date(startAt.get()))).append(lineSeparator).append(banner).append(lineSeparator);

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == attributes) {
            return;
        }

        MethodSignature ms = (MethodSignature) point.getSignature();
        Method func = ms.getMethod();
        String className = ms.getDeclaringTypeName();
        Object[] args = point.getArgs();

        String funcSign = String.format("%s %s(%s)", func.getReturnType().getSimpleName(), func.getName(), String.join(", ", getArguments(ms)));

        sb.append(space).append("---- API").append(lineSeparator)
            .append(space).append("Class:").append(space).append(className).append(lineSeparator)
            .append(space).append("Function:").append(space).append(funcSign).append(lineSeparator)
            .append(space).append("Args:").append(space).append(Arrays.toString(args)).append(lineSeparator)
            .append(space).append("Desc:").append(space).append(getDesc(point)).append(lineSeparator);

        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURL().toString();
        String method = request.getMethod();

        sb.append(lineSeparator).append(space).append("******** Request Start").append(lineSeparator)
            .append(space).append("URL:").append(space).append(url).append(lineSeparator)
            .append(space).append("Method:").append(space).append(method).append(lineSeparator)
            .append(space).append("At:").append(space).append(FORMAT_DATE.format(new Date(startAt.get()))).append(lineSeparator);

        sb.append(lineSeparator).append(space).append("---- Request Header").append(lineSeparator);
        Map<String, String> headers = parseServletHeaders(request);
        headers.forEach((name, value) -> sb.append(space).append(name).append(":").append(space).append(value).append(lineSeparator));

        sb.append(lineSeparator).append(space).append("---- Request Query").append(lineSeparator);
        String query = request.getQueryString();
        if (StringUtils.hasText(query)) {
            sb.append(space).append(query).append(lineSeparator);
        }

        sb.append(lineSeparator).append(space).append("---- Request Body").append(lineSeparator);
        RequestWrapper wrapped = wrappedRequest(request);
        if (null != wrapped) {
            byte[] body = wrapped.body();
            if (null != body && body.length > 0) {
                sb.append(space).append(new String(body, StandardCharsets.UTF_8).replace(lineSeparator, lineSeparator + space)).append(lineSeparator);
            }
        }
        sb.append(lineSeparator).append(space).append("******** Request End").append(lineSeparator);

        message.set(sb);
        attributesTL.set(attributes);
    }

    /**
     * 后置增强
     *
     * @param result 方法返回的结果
     * @throws JsonProcessingException json 转换异常
     */
    @AfterReturning(value = "webLog()", returning = "result")
    public void after(Object result) throws JsonProcessingException {
        handlerAfter(result);
    }

    /**
     * 后置增强
     * <p>
     * 统一异常处理器
     *
     * @param result 方法返回的结果
     * @throws JsonProcessingException json 转换异常
     */
    @AfterReturning(value = "expLog()", returning = "result")
    public void afterExp(Object result) throws JsonProcessingException {
        handlerAfter(result);
    }

    private void handlerAfter(Object result) throws JsonProcessingException {
        StringBuffer sb = message.get();
        if (null == sb) {
            // 说明没有进过 controller，此处先不处理
            return;
        }
        HttpServletResponse response = attributesTL.get().getResponse();

        sb.append(lineSeparator).append(space).append("******** Response Start").append(lineSeparator)
            .append(space).append("---- Response Header").append(lineSeparator);

        Map<String, Collection<String>> headers = parseServletHeaders(response);
        headers.forEach((name, value) -> sb.append(space).append(name).append(":").append(space).append(value).append(lineSeparator));

        sb.append(lineSeparator).append(space).append("---- Response Body").append(lineSeparator);
        sb.append(space).append(objectMapper.writeValueAsString(result).replace(lineSeparator, lineSeparator + space)).append(lineSeparator);

        long end = SystemClock.now();
        sb.append(lineSeparator).append(space).append("******** Response End").append(lineSeparator)
            .append(space).append("At:").append(space).append(FORMAT_DATE.format(new Date(end))).append(lineSeparator)
            .append(space).append("Latency:").append(space).append(end - startAt.get()).append(" ms").append(lineSeparator);

        sb.append(footer.replace("{}", getTraceId()));

        log.info(sb.toString());

        startAt.remove();
        message.remove();
        attributesTL.remove();
    }

    private String[] getArguments(MethodSignature ms) {
        if (null == ms || null == ms.getParameterTypes()) {
            return new String[0];
        }

        String[] result = new String[ms.getParameterTypes().length];
        Class<?> type;
        String name;
        for (int i = 0; i < ms.getParameterTypes().length; i++) {
            type = ms.getParameterTypes()[i];
            name = ms.getParameterNames()[i];
            result[i] = type.getSimpleName() + " " + name;
        }
        return result;
    }

    /**
     * 递归获取已包装的 request 对象
     * <p>
     * 实际开发中可能 request 被多种框架多重包装，此时无法确定最外层包装类型，无法保证最外层是 {@link RequestWrapper}
     * 即不能直接使用 {@code request instanceof RequestWrapper} 来判断
     * 需要递归判断 {@link ServletRequestWrapper#getRequest()} 获取到的 {@code request} 实例
     *
     * @param req 原始请求
     * @return wrapper
     */
    private RequestWrapper wrappedRequest(@NotNull ServletRequest req) {
        if (!(req instanceof ServletRequestWrapper)) {
            // 请求未被包装
            return null;
        }
        if (req instanceof RequestWrapper) {
            // 满足当前包装类
            return (RequestWrapper) req;
        }

        ServletRequest request = ((ServletRequestWrapper) req).getRequest();
        if (request.equals(req)) {
            return null;
        }

        // 递归
        return wrappedRequest(request);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private String getDesc(JoinPoint point) {
        String className = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        Object[] args = point.getArgs();
        try {
            Class<?> clazz = Class.forName(className);
            Method[] methods = clazz.getMethods();
            Class apiOperation;
            Annotation ann;
            Method temp;
            for (Method md : methods) {
                if (md.getName().equals(methodName) && md.getParameterCount() == args.length) {
                    // 就是他了
                    if (md.isAnnotationPresent(HttpLog.class)) {
                        return md.getAnnotation(HttpLog.class).value();
                    }
                    try {
                        // swagger2
                        apiOperation = Class.forName("io.swagger.annotations.ApiOperation");
                        if (md.isAnnotationPresent(apiOperation)) {
                            ann = md.getAnnotation(apiOperation);
                            temp = apiOperation.getMethod("value");
                            return temp.invoke(ann).toString();
                        }
                    } catch (Exception ignored) {
                    }
                    try {
                        // swagger3
                        apiOperation = Class.forName("io.swagger.v3.oas.annotations.Operation");
                        if (md.isAnnotationPresent(apiOperation)) {
                            ann = md.getAnnotation(apiOperation);
                            temp = apiOperation.getMethod("summary");
                            return temp.invoke(ann).toString();
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    @NotNull
    private Map<String, Collection<String>> parseServletHeaders(HttpServletResponse response) {
        if (null == response) {
            return Maps.of();
        }
        Map<String, Collection<String>> headers = new HashMap<>();
        Collection<String> names = response.getHeaderNames();
        names.forEach(name -> headers.put(name, response.getHeaders(name)));
        return headers;
    }

    @NotNull
    private Map<String, String> parseServletHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        String name;
        while (names.hasMoreElements()) {
            name = names.nextElement();
            headers.put(name, request.getHeader(name));
        }
        return headers;
    }

    private String getTraceId() {
        String traceId = MDC.get(Define.TRACE_ID);
        if (StringUtils.hasText(traceId)) {
            return traceId;
        }
        return "#";
    }
}
