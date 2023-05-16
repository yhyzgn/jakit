package com.yhy.jakit.simple.starter.module.common;

import com.yhy.jakit.simple.support.model.Res;
import com.yhy.jakit.starter.aop.limit.Limiter;
import com.yhy.jakit.starter.helper.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2022-07-29 10:14
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    // @Autowired
    // private RedisHelper redisHelper;

    @Limiter(key = "common-index", period = 60, quota = 4)
    @GetMapping
    public Res index() {
        // log.info("{}", redisHelper);
        return Res.success();
    }
}
