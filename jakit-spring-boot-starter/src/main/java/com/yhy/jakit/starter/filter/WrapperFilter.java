package com.yhy.jakit.starter.filter;

import com.yhy.jakit.starter.wrapper.RequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * 对 ServletRequest 进行包装，达到 RequestBody 多次读取的目的
 * <p>
 * 只提供组件，使用时需要在具体项目 FilterRegistrationBean 中配置
 * <p>
 * Created on 2022-07-26 16:49
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
@WebFilter(value = "/", filterName = "internal-wrapper-filter")
public class WrapperFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("ComponentWrapperFilter has been init.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(RequestWrapper.wrap(request), response);
    }
}