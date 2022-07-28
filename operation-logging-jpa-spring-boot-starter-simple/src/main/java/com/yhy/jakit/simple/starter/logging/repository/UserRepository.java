package com.yhy.jakit.simple.starter.logging.repository;

import com.yhy.jakit.simple.starter.logging.model.UserEntity;
import com.yhy.jakit.starter.orm.JakitRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-12-10 9:56
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JakitRepository<UserEntity, Long> {
}
