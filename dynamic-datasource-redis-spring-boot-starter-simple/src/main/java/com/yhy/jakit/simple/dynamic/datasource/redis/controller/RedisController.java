package com.yhy.jakit.simple.dynamic.datasource.redis.controller;

import com.yhy.jakit.simple.support.model.Res;
import com.yhy.jakit.util.system.SystemClock;
import com.yhy.jakit.starter.dynamic.datasource.redis.dynamic.DynamicStringRedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2021-05-25 16:23
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private DynamicStringRedisTemplate dynamicTemplate;

    @GetMapping
    public Res test(String name) {
        StringRedisTemplate template = dynamicTemplate.current();
        String keyPrefix = dynamicTemplate.currentKeyPrefix();
        template.opsForValue().set(keyPrefix + ":dynamic-test", "Dynamic datasource test data " + name + " :: " + SystemClock.nowDate());

        Object obj = template.opsForValue().get(keyPrefix + ":dynamic-test");
        return Res.success(obj);
    }
}

