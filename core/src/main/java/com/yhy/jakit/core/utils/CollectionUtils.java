package com.yhy.jakit.core.utils;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * 集合工具类
 * <p>
 * Created on 2021-08-16 14:55
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class CollectionUtils {

    /**
     * 集合是否为空
     *
     * @param collection 集合
     * @return 是否为空
     */
    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return null == collection || collection.isEmpty();
    }

    /**
     * Map是否为空
     *
     * @param map Map
     * @return 是否为空
     */
    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return null == map || map.isEmpty();
    }

    /**
     * 集合是否非空
     *
     * @param collection 集合
     * @return 是否非空
     */
    public static boolean isNotEmpty(@Nullable Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Map是否非空
     *
     * @param map Map
     * @return 是否非空
     */
    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return !isEmpty(map);
    }
}
