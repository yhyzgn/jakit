package com.yhy.jakit.starter.crypto.component.enums;

/**
 * 填充
 * <p>
 * Created on 2021-03-24 11:04
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public enum Padding {
    NO("NoPadding"),
    ZERO("ZeroPadding"),
    PKCS1("PKCS1Padding"),
    PKCS5("PKCS5Padding"),
    PKCS7("PKCS7Padding"),
    ISO10126("ISO10126Padding"),
    OAEP("OAEPPadding"),
    SSL3("SSL3Padding"),
    ;

    private final String value;

    Padding(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
