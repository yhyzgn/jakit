package com.yhy.jakit.util.internal;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 键值对载体
 * <p>
 * Created on 2023-06-06 16:05
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class Pair<S, T> implements Serializable {
    private final S first;
    private final T second;

    private Pair(S first, T second) {
        this.first = first;
        this.second = second;
    }

    /**
     * 构建一个键值对
     *
     * @param first  第一个元素
     * @param second 第二个元素
     * @param <S>    第一个元素类型
     * @param <T>    第二个元素类型
     * @return 键值对对象
     */
    public static <S, T> Pair<S, T> of(S first, T second) {
        return new Pair<>(first, second);
    }

    /**
     * 获取第一个元素
     *
     * @return 第一个元素
     */
    public S getFirst() {
        return first;
    }

    /**
     * 获取第二个元素
     *
     * @return 第二个元素
     */
    public T getSecond() {
        return second;
    }

    /**
     * 将键值对转 Map
     *
     * @param <S> 第一个元素类型
     * @param <T> 第二个元素类型
     * @return Map 集合
     */
    public static <S, T> Collector<Pair<S, T>, ?, Map<S, T>> toMap() {
        return Collectors.toMap(Pair::getFirst, Pair::getSecond);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) o;
        if (!Objects.equals(first, pair.first)) {
            return false;
        }
        return Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(first);
        result = 31 * result + Objects.hashCode(second);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s->%s", this.first, this.second);
    }
}
