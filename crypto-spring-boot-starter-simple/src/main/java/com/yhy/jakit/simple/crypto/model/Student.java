package com.yhy.jakit.simple.crypto.model;

import com.yhy.jakit.starter.crypto.annotation.Decrypt;
import com.yhy.jakit.starter.crypto.annotation.Encrypt;
import lombok.Data;

/**
 * 入参测试
 * <p>
 * Created on 2021-03-31 9:47
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class Student {

    private Long id;

    @Decrypt
    private String name;

    @Encrypt
    @Decrypt
    private String mobile;

    @Encrypt
    @Decrypt
    private String idCard;

    private Subject subject;
}
