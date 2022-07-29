package com.yhy.jakit.starter.helper.internal;

import com.yhy.jakit.starter.dynamic.datasource.redis.dynamic.DynamicRedisConnectionFactory;
import com.yhy.jakit.starter.dynamic.datasource.redis.dynamic.DynamicStringRedisTemplate;
import com.yhy.jakit.starter.helper.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
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
@Primary
@Component
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 1)
@AutoConfigureAfter(DynamicStringRedisTemplate.class)
@ConditionalOnClass({DynamicRedisConnectionFactory.class, DynamicStringRedisTemplate.class})
@ConditionalOnMissingBean(DynamicStringRedisTemplate.class)
public class RedisHelperDynamicTemplate extends RedisHelper implements InitializingBean {
    @Autowired
    private DynamicStringRedisTemplate template;

    @Override
    public StringRedisTemplate template() {
        return template.current();
    }

    @Override
    public String keyPrefix() {
        return template.currentKeyPrefix();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Current RedisTemplate is 'DynamicStringRedisTemplate'.");
    }
}
