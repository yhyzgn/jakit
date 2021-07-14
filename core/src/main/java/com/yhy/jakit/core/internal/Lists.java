package com.yhy.jakit.core.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * List 快速构建工具
 * <p>
 * Created on 2021-07-13 10:36
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Lists {

    /**
     * 快速创建一个 List
     * <p>
     * 用 {@link ArrayList} 实现
     *
     * @param t   元素列表
     * @param <T> 元素类型
     * @return List
     */
    @SafeVarargs
    public static <T> List<T> of(T... t) {
        List<T> list = new ArrayList<>();
        if (null != t && t.length > 0) {
            list.addAll(Arrays.asList(t));
        }
        return list;
    }
}
