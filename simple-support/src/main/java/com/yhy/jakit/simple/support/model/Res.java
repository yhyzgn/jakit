package com.yhy.jakit.simple.support.model;

import lombok.Data;

/**
 * Created on 2022-07-27 17:01
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class Res {
    private int code;
    private String msg;
    private Object data;

    private Res(int code) {
        this(code, null);
    }

    private Res(int code, String msg) {
        this(code, msg, null);
    }

    private Res(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Res success() {
        return success(null, null);
    }

    public static Res success(String msg) {
        return success(msg, null);
    }

    public static Res success(Object data) {
        return success(null, data);
    }

    public static Res success(String msg, Object data) {
        return new Res(0, msg, data);
    }

    public static Res failure() {
        return failure(-1);
    }

    public static Res failure(int code) {
        return failure(code, null);
    }

    public static Res failure(String msg) {
        return failure(-1, msg);
    }

    public static Res failure(int code, String msg) {
        return new Res(code, msg);
    }
}
