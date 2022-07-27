package com.yhy.jakit.simple.dynamic.datasource.mongo.repository;

import com.yhy.jakit.simple.dynamic.datasource.mongo.entity.UserEntity;
import com.yhy.jakit.starter.dynamic.datasource.mongo.repository.DynamicMongoRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.function.Function;

/**
 * Created on 2021-04-21 16:29
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public class UserRepository extends DynamicMongoRepository<UserEntity, String> {
    @Override
    public <S extends UserEntity, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}
