package com.yhy.jakit.starter.crypto.exception;

import com.yhy.jakit.starter.crypto.component.enums.Algorithm;

/**
 * 不支持的加密算法
 * <p>
 * Created on 2021-05-31 17:41
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class UnSupportedCryptAlgorithmException extends RuntimeException {

    public UnSupportedCryptAlgorithmException(Algorithm algorithm) {
        super("Unsupported crypt algorithm '" + algorithm.getName() + "'");
    }
}