package com.yhy.jakit.simple.dynamic.datasource.mongo.controller;

import com.yhy.jakit.simple.support.model.Res;
import com.yhy.jakit.simple.dynamic.datasource.mongo.entity.UserEntity;
import com.yhy.jakit.simple.dynamic.datasource.mongo.repository.UserRepository;
import com.yhy.jakit.starter.dynamic.datasource.mongo.dynamic.DynamicMongoTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * Created on 2021-04-21 16:28
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/mongo")
public class MongoController {
    private final static Random RANDOM = new Random();

    @Autowired
    private UserRepository repository;
    @Autowired
    private DynamicMongoTemplate dynamicMongoTemplate;

    @GetMapping
    public Res test(String name) {
        dynamicMongoTemplate.current();
        UserEntity user = UserEntity.builder()
            .name(name)
            .age(RANDOM.nextInt(100))
            .build();
        return Res.success(repository.save(user));
    }
}
