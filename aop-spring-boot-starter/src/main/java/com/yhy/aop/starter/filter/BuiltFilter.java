package com.yhy.aop.starter.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 注入一些构建信息
 * <p>
 * Created on 2022-07-26 16:50
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@WebFilter(value = "/", filterName = "internal-built-filter")
public class BuiltFilter implements Filter {
    private static final SimpleDateFormat pattern = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

    private Date startAt;

    @Value("${spring.application.name:}")
    private String name;

    @Value("${spring.application.version:}")
    private String version;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        startAt = new Date();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (!StringUtils.hasText(version) || Pattern.matches("^\\{[\\w\\-]+}$", version)) {
            version = pattern.format(new Date()) + "_1.0.0.dev";
        }

        // 嵌入版本信息
        res.setHeader("RP-Build-Version", name + "__" + version);
        res.setHeader("RP-Start", pattern.format(startAt));

        chain.doFilter(req, res);
    }
}