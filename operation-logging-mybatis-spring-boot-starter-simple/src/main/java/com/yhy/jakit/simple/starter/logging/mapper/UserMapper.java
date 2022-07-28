package com.yhy.jakit.simple.starter.logging.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yhy.jakit.simple.starter.logging.model.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created on 2021-12-10 9:56
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
