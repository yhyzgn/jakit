package com.yhy.jakit.simple.dynamic.datasource.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yhy.jakit.simple.dynamic.datasource.mybatis.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created on 2022-06-14 15:06
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
