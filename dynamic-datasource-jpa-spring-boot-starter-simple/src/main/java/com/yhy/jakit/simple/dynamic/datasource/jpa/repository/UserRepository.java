package com.yhy.jakit.simple.dynamic.datasource.jpa.repository;

import com.yhy.jakit.simple.dynamic.datasource.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-04-21 16:29
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
