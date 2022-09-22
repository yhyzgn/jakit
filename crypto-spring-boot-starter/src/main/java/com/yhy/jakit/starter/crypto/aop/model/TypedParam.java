package com.yhy.jakit.starter.crypto.aop.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Parameter;

/**
 * Created on 2022-09-22 9:39
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@AllArgsConstructor
@Data
public class TypedParam {
    private final int index;
    private final Class<?> type;
    private final String name;
    private final Object value;
    private final Parameter parameter;
}
