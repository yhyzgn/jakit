package com.yhy.jakit.core.descriptor.consumer;

import java.util.function.BiConsumer;
import java.util.function.ObjLongConsumer;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2020-05-02 10:57 下午
 * version: 1.0.0
 * desc   : 处理一个 long 类型的参数
 *
 * @see BiConsumer
 * @since 1.8
 */
@FunctionalInterface
public interface OBJLongConsumer<T> extends ObjLongConsumer<T> {
}
