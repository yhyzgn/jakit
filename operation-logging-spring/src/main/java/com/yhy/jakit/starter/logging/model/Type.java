package com.yhy.jakit.starter.logging.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 操作日志类型
 * <p>
 * Created on 2020-11-02 17:36
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Type {
    Unknown(0, "未知"),
    Insert(100, "新增"),
    Update(101, "变更"),
    Select(102, "查询"),
    Delete(103, "删除");

    private final Integer code;
    private final String name;

    public static Type parse(int code) {
        return Arrays.stream(Type.values()).filter(type -> type.code.equals(code)).findFirst().orElse(Type.Unknown);
    }
}

