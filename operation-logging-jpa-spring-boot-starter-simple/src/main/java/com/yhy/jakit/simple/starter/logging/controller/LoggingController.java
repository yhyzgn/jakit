package com.yhy.jakit.simple.starter.logging.controller;

import com.yhy.jakit.simple.starter.logging.model.UserEntity;
import com.yhy.jakit.simple.starter.logging.service.UserService;
import com.yhy.jakit.simple.support.model.Res;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/logging/jpa")
public class LoggingController {
    @Autowired
    private UserService service;

    @GetMapping
    public Res test() {
        UserEntity entity = new UserEntity();
        entity.setName("张三");
        entity.setAge(23);
        entity = service.save(entity);
        return Res.success(entity);
    }
}
