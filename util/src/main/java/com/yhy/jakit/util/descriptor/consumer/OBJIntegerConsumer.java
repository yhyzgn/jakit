package com.yhy.jakit.util.descriptor.consumer;

import java.util.function.BiConsumer;
import java.util.function.ObjIntConsumer;

/**
 * 处理一个 int 类型的参数
 * <p>
 * Created on 2020-05-02 22:56
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @see BiConsumer
 * @since 1.0.0
 */
@FunctionalInterface
public interface OBJIntegerConsumer<T> extends ObjIntConsumer<T> {
}
