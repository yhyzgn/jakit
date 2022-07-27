package com.yhy.jakit.starter.exception;

/**
 * Created on 2022-07-26 17:04
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class DuplicateException extends RuntimeException {
    public DuplicateException() {
        this("数据重复");
    }

    public DuplicateException(String message) {
        super(message);
    }
}