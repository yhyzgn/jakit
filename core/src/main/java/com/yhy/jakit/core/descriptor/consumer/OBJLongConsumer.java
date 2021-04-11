package com.yhy.jakit.core.descriptor.consumer;

import java.util.function.BiConsumer;
import java.util.function.ObjLongConsumer;

/**
 * 处理一个 long 类型的参数
 * <p>
 * Created on 2020-05-02 22:57
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @see BiConsumer
 * @since 1.0.0
 */
@FunctionalInterface
public interface OBJLongConsumer<T> extends ObjLongConsumer<T> {
}
