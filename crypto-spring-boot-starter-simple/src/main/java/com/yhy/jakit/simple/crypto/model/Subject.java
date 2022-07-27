package com.yhy.jakit.simple.crypto.model;

import com.yhy.jakit.starter.crypto.annotation.Decrypt;
import com.yhy.jakit.starter.crypto.annotation.Encrypt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created on 2021-03-31 16:12
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subject {

    @Encrypt
    @Decrypt
    private String code;

    @Encrypt
    private String name;
}
