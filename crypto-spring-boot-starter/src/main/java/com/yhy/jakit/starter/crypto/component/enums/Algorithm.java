package com.yhy.jakit.starter.crypto.component.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 加密算法
 * <p>
 * Created on 2021-05-31 14:37
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum Algorithm {
    // 对称加密
    AES("AES"),
    DES("DES"),
    DESede("DESede"),
    RC4("RC4"),
    SM4("SM4"),
    Vigenere("Vigenere"),

    // 非对称加密
    RSA("RSA"),
    SM2("SM2"),
    ECIES("ECIES"),

    // 摘要签名算法
    MD5("MD5"),
    SM3("SM3"),
    SHA1("SHA1"),
    SHA256("SHA256"),
    ;

    private final String name;


    /**
     * 获得算法的字符串表示形式
     *
     * @return 算法字符串
     */
    public String getName() {
        return name;
    }
}
