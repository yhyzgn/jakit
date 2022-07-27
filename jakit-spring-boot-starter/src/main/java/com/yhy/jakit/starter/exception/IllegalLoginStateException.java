package com.yhy.jakit.starter.exception;

/**
 * Created on 2022-07-26 17:23
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class IllegalLoginStateException extends RuntimeException {
    public IllegalLoginStateException() {
        this("请先登录");
    }

    public IllegalLoginStateException(String message) {
        super(message);
    }
}
