package com.yhy.jakit.util.descriptor.consumer;

import java.util.function.BiConsumer;

/**
 * 处理一个 boolean 类型的参数
 * <p>
 * Created on 2020-05-02 22:59
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @see BiConsumer
 * @since 1.0.0
 */
@FunctionalInterface
public interface OBJBooleanConsumer<T> {

    /**
     * 对给定参数执行此操作
     *
     * @param t     参数1
     * @param value 参数2
     */
    void accept(T t, boolean value);
}
