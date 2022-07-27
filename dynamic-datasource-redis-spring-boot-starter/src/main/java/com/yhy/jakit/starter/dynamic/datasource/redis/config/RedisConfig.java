package com.yhy.jakit.starter.dynamic.datasource.redis.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 2021-05-25 16:43
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfig {

    /**
     * Spring redis 配置项
     */
    @Autowired(required = false)
    private RedisProperties properties;

    /**
     * key 前缀
     */
    private String keyPrefix;
}
