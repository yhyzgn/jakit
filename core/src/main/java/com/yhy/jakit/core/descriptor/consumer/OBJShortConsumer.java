package com.yhy.jakit.core.descriptor.consumer;

import java.util.function.BiConsumer;

/**
 * 处理一个 short 类型的参数
 * <p>
 * Created on 2020-05-02 22:58
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @see BiConsumer
 * @since 1.0.0
 */
@FunctionalInterface
public interface OBJShortConsumer<T> {

    /**
     * 对给定参数执行此操作
     *
     * @param t     参数1
     * @param value 参数2
     */
    void accept(T t, short value);
}
