package com.yhy.jakit.simple.dynamic.datasource.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created on 2021-04-21 11:14
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("dy_user")
public class UserEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name;

    private Integer age;
}
