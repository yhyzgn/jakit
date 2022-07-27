package com.yhy.jakit.simple.dynamic.datasource.redis.config;

import com.yhy.jakit.simple.dynamic.datasource.redis.interceptor.DynamicRedisTemplateInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created on 2021-04-21 16:27
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private DynamicRedisTemplateInterceptor templateInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(templateInterceptor).addPathPatterns("/redis/**");
    }
}
