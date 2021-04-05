package com.yhy.jakit.core.descriptor.consumer;

import java.util.function.BiConsumer;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2020-05-02 11:01 下午
 * version: 1.0.0
 * desc   : 处理一个 float 类型的参数
 *
 * @see BiConsumer
 * @since 1.8
 */
@FunctionalInterface
public interface OBJFloatConsumer<T> {

    /**
     * 对给定参数执行此操作
     *
     * @param t     参数1
     * @param value 参数2
     */
    void accept(T t, float value);
}
