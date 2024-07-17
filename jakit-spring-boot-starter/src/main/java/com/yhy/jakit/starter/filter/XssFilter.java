package com.yhy.jakit.starter.filter;

import cn.hutool.core.util.StrUtil;
import com.yhy.jakit.starter.wrapper.RequestWrapper;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * xss 拦截器
 * <p>
 * Created on 2024-04-19 13:56
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@WebFilter(value = "/", filterName = "xss-filter", asyncSupported = true)
public class XssFilter implements Filter {

    /**
     * 排除链接
     */
    public List<String> excludes = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String tempExcludes = filterConfig.getInitParameter("excludes");
        if (StrUtil.isNotEmpty(tempExcludes)) {
            String[] url = tempExcludes.split(",");
            excludes.addAll(Arrays.asList(url));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if (handleExcludeUrl(req, resp)) {
            chain.doFilter(request, response);
            return;
        }
        chain.doFilter(RequestWrapper.wrap(request), response);
    }

    private boolean handleExcludeUrl(HttpServletRequest request, HttpServletResponse response) {
        String url = request.getServletPath();
        String method = request.getMethod();
        // GET DELETE 不过滤
        boolean isFilter = method == null || method.matches("GET") || method.matches("DELETE");
        if (isFilter) {
            return true;
        }
        return excludes.stream().anyMatch(s -> new AntPathMatcher().match(s, url));
    }
}
