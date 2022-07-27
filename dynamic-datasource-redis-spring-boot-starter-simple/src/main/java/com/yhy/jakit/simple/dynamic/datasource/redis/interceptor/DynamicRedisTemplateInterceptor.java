package com.yhy.jakit.simple.dynamic.datasource.redis.interceptor;

import com.yhy.jakit.starter.dynamic.datasource.redis.config.RedisConfig;
import com.yhy.jakit.starter.dynamic.datasource.redis.dynamic.DynamicStringRedisTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2021-04-21 16:23
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
public class DynamicRedisTemplateInterceptor implements HandlerInterceptor {
    private final static Map<String, RedisConfig> SOME_CONFIG = new HashMap<>();
    @Autowired
    private DynamicStringRedisTemplate dynamicTemplate;

    // 构造一些测试配置
    static {
        String name, prefix;
        RedisProperties properties;
        RedisConfig config;
        for (int i = 10; i < 16; i++) {
            name = "redis-" + i;
            prefix = "kf" + i;

            properties = new RedisProperties();
            properties.setClientName(name);
            properties.setHost("localhost");
            properties.setPassword("root");
            properties.setDatabase(i);

            config = new RedisConfig();
            config.setProperties(properties);
            config.setKeyPrefix(prefix);

            SOME_CONFIG.put(name, config);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String db = request.getHeader("DB");
        String name = "redis-" + db;

        if (StringUtils.hasText(db)) {
            if (db.equals("def")) {
                // 切换到 默认数据源
                dynamicTemplate.switchToDefault();
                log.info("已切换到默认数据源");
            } else {
                if (dynamicTemplate.exist(name)) {
                    dynamicTemplate.switchTo(name);
                    log.info("已切换到数据源：" + name);
                } else {
                    // 动态添加并切换到 其他数据源
                    RedisConfig properties = SOME_CONFIG.get(name);
                    Assert.notNull(properties, "配置不存在");
                    dynamicTemplate.addAndSwitchTo(properties);
                    log.info("已动态创建并切换到新数据源：" + name);
                }
            }
        } else {
            dynamicTemplate.switchToDefault();
            log.info("已切换到默认数据源");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        dynamicTemplate.destroy();
    }
}
