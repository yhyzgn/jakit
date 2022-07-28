package com.yhy.jakit.simple.starter.logging.model;

import com.yhy.jakit.starter.logging.jpa.listener.LoggingListener;
import lombok.Data;

import javax.persistence.*;

/**
 * Created on 2021-12-10 9:52
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Entity(name = "user")
@EntityListeners(LoggingListener.class)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer age;
}
