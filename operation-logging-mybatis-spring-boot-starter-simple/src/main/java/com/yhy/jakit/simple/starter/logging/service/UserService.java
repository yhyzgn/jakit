package com.yhy.jakit.simple.starter.logging.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhy.jakit.simple.starter.logging.mapper.UserMapper;
import com.yhy.jakit.simple.starter.logging.model.UserEntity;
import com.yhy.jakit.starter.logging.annotation.Logging;
import com.yhy.jakit.util.internal.Lists;
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
public class UserService extends ServiceImpl<UserMapper, UserEntity> {
    @Autowired
    private UserMapper mapper;

    @Logging("创建用户")
    public UserEntity saveUser(UserEntity entity) {
        UserEntity user = mapper.selectById(entity.getId());
        if (null != user) {
            user.setName(entity.getName());
            user.setAge(entity.getAge());
            updateBatchById(Lists.of(user));
            // updateById(user);
            return user;
        }
        mapper.insert(entity);
        return entity;
    }
}
