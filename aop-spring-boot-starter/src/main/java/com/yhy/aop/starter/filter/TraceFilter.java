package com.yhy.aop.starter.filter;

import com.yhy.jakit.util.HttpUtils;
import com.yhy.jakit.util.RandUtils;
import com.yhy.jakit.util.cst.Define;
import com.yhy.jakit.util.internal.Trace;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Trace 拦截器
 * <p>
 * Created on 2022-07-28 10:35
 * <p>
 * 响应时得在 AOP 中添加，否则无效
 * <p>
 * 参考 {@link com.yhy.aop.starter.aop.trace.TraceAspect}
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@WebFilter(value = "/", filterName = "internal-trace-filter")
public class TraceFilter implements Filter {
    @Value("${spring.application.name:}")
    private String appName;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 先清空吧
        Trace.clear();

        // 加入 TraceID & SpanID & SpanChain
        String traceId = HttpUtils.getHeader(req, Define.TRACE_ID);
        String spanId = StringUtils.hasText(appName) ? appName : RandUtils.getString(6);
        String spanChain = HttpUtils.getHeader(req, Define.SPAN_CHAIN);
        traceId = StringUtils.hasText(traceId) ? traceId : RandUtils.getString(16);
        spanChain = StringUtils.hasText(spanChain) ? spanChain + "." + spanId : spanId;

        Trace.put(Define.TRACE_ID, traceId);
        Trace.put(Define.SPAN_ID, spanId);
        Trace.put(Define.SPAN_CHAIN, spanChain);

        // 响应时得在 AOP 中添加，否则无效
        // 参考 TraceAspect

        chain.doFilter(req, res);
    }
}
