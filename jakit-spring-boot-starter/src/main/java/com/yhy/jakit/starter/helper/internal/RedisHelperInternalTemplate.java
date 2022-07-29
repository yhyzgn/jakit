package com.yhy.jakit.starter.helper.internal;

import com.yhy.jakit.starter.helper.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.Ordered;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Created on 2022-07-29 9:34
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@AutoConfigureAfter({StringRedisTemplate.class})
@ConditionalOnClass({RedisConnectionFactory.class, StringRedisTemplate.class})
public class RedisHelperInternalTemplate extends RedisHelper implements InitializingBean {
    @Autowired
    private StringRedisTemplate template;

    @Override
    public StringRedisTemplate template() {
        return template;
    }

    @Override
    public String keyPrefix() {
        return "";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Current RedisTemplate is 'StringRedisTemplate'.");
    }
}
