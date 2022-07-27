package com.yhy.jakit.simple.dynamic.datasource.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document("dy_user")
public class UserEntity {

    @Id
    private String id;

    private String name;

    private Integer age;
}
