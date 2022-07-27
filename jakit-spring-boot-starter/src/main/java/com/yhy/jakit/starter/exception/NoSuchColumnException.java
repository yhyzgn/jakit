package com.yhy.jakit.starter.exception;

/**
 * Created on 2022-07-26 17:24
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class NoSuchColumnException extends RuntimeException {
    public NoSuchColumnException(String column) {
        super("No such column '" + column + "' found.");
    }
}
