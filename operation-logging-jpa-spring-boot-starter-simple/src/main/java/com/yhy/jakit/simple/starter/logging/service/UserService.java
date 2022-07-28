package com.yhy.jakit.simple.starter.logging.service;

import com.yhy.jakit.simple.starter.logging.model.UserEntity;
import com.yhy.jakit.simple.starter.logging.repository.UserRepository;
import com.yhy.jakit.starter.logging.annotation.Logging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2021-12-10 9:55
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    @Logging("创建用户")
    public UserEntity save(UserEntity entity) {
        return repository.save(entity);
    }
}
