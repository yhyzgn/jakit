package com.yhy.jakit.simple.dynamic.datasource.mongo.interceptor;

import com.yhy.jakit.starter.dynamic.datasource.mongo.config.MongoDBProperties;
import com.yhy.jakit.starter.dynamic.datasource.mongo.dynamic.DynamicMongoTemplate;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DataSourceInterceptor implements HandlerInterceptor {
    private static final Map<String, MongoDBProperties> PROP_MAP = new HashMap<>();

    @Autowired
    private DynamicMongoTemplate mongoTemplate;

    // 构造一些测试配置
    static {
        String name;
        MongoDBProperties properties;
        for (int i = 0; i < 5; i++) {
            name = "dynamic-" + i;
            properties = MongoDBProperties.builder()
                .name(name)
                .host("localhost")
                .port(27017)
                .database(name)
                .username("root")
                .password("root")
                .authenticationDatabase("admin")
                .build();
            PROP_MAP.put(name, properties);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        String db = request.getHeader("DB");
        String name = "dynamic-" + db;

        if (StringUtils.hasText(db)) {
            if (db.equals("def")) {
                // 切换到 默认数据源
                mongoTemplate.switchToDefault();
                log.info("已切换到默认数据源");
            } else {
                if (mongoTemplate.exist(name)) {
                    mongoTemplate.switchTo(name);
                    log.info("已切换到数据源：" + name);
                } else {
                    // 动态添加并切换到 其他数据源
                    MongoDBProperties properties = PROP_MAP.get(name);
                    Assert.notNull(properties, "配置不存在");
                    mongoTemplate.addAndSwitchTo(properties);
                    log.info("已动态创建并切换到新数据源：" + name);
                }
            }
        } else {
            mongoTemplate.switchToDefault();
            log.info("已切换到默认数据源");
        }
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) throws Exception {
        mongoTemplate.destroy();
    }
}
