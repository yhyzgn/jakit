package com.yhy.jakit.simple.starter.logging.controller;

import com.yhy.jakit.simple.starter.logging.model.UserEntity;
import com.yhy.jakit.simple.starter.logging.service.UserService;
import com.yhy.jakit.simple.support.model.Res;
import com.yhy.jakit.starter.logging.mybatis.interceptor.LoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2021-12-01 13:30
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/logging/mybatis")
public class LoggingController {
    @Autowired
    private UserService service;

    @Autowired
    private LoggingInterceptor interceptor;

    @PostMapping
    public Res test(@RequestBody UserEntity user) {
        user = service.saveUser(user);
        return Res.success(user);
    }
}
