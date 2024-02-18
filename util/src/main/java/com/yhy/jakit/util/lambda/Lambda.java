package com.yhy.jakit.util.lambda;

import java.io.Serializable;
import java.util.function.Function;

/**
 * λ 表达式
 * <p>
 * Created on 2024-02-18 23:24
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface Lambda<T, R> extends Serializable, Function<T, R> {
}
