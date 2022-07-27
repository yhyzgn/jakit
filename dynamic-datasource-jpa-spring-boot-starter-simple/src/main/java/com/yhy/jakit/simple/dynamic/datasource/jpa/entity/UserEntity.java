package com.yhy.jakit.simple.dynamic.datasource.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
@Entity
@Table(name = "dy_user")
public class UserEntity {

    @Id
    @Column(name = "id", columnDefinition = "BIGINT(20) COMMENT 'ID主键'")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "VARCHAR(255) COMMENT '姓名'")
    private String name;

    @Column(name = "age", columnDefinition = "INT(11) COMMENT '年龄'")
    private Integer age;
}
