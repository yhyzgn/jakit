package com.yhy.jakit.util.descriptor.consumer;

import java.util.function.BiConsumer;

/**
 * 处理一个 float 类型的参数
 * <p>
 * Created on 2020-05-02 23:01
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @see BiConsumer
 * @since 1.0.0
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
