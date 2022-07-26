package com.yhy.jakit.util.internal;

/**
 * 字符串工具类
 * <p>
 * Created on 2022-01-06 16:04
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Strings {

    /**
     * 获取 {@link StringBuilder} 实例
     *
     * @return StringBuilder
     */
    static StringBuilder builder() {
        return new StringBuilder();
    }

    /**
     * 获取 {@link StringBuilder} 实例
     *
     * @return StringBuilder
     */
    static StringBuilder builder(String str) {
        return new StringBuilder(str);
    }

    /**
     * 获取 {@link StringBuffer} 实例
     *
     * @return StringBuffer
     */
    static StringBuffer buffer() {
        return new StringBuffer();
    }

    /**
     * 获取 {@link StringBuffer} 实例
     *
     * @return StringBuffer
     */
    static StringBuffer buffer(String str) {
        return new StringBuffer(str);
    }
}
