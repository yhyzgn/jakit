package com.yhy.jakit.starter.dynamic.datasource.redis;

import com.yhy.jakit.starter.dynamic.datasource.redis.config.RedisConfig;
import com.yhy.jakit.starter.dynamic.datasource.redis.dynamic.DynamicRedisConnectionFactory;
import com.yhy.jakit.starter.dynamic.datasource.redis.dynamic.DynamicStringRedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Created on 2021-05-25 16:03
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(RedisConnectionFactory.class)
@ComponentScan
public class DynamicDataSourceRedisAutoConfiguration {
    @Autowired(required = false)
    private RedisConnectionFactory defaultFactory;
    @Autowired(required = false)
    private StringRedisTemplate defaultTemplate;
    @Autowired(required = false)
    private RedisConfig defaultConfig;

    @Bean
    @ConditionalOnMissingBean
    public DynamicRedisConnectionFactory dynamicRedisConnectionFactory() {
        return new DynamicRedisConnectionFactory(defaultFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicStringRedisTemplate dynamicRedisStringTemplate(DynamicRedisConnectionFactory factory) {
        return new DynamicStringRedisTemplate(defaultConfig, factory, defaultTemplate);
    }
}
