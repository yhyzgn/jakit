package com.yhy.jakit.starter.crypto.exception;

/**
 * Created on 2021-06-05 10:15
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class CryptoException extends RuntimeException {

    public CryptoException() {
        this("加密解密失败");
    }

    public CryptoException(String msg) {
        super(msg);
    }
}
