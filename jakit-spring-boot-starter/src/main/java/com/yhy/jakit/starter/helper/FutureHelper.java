package com.yhy.jakit.starter.helper;

import com.alibaba.ttl.TtlRunnable;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * CompletableFuture 辅助类
 * <p>
 * Created on 2022-07-26 17:29
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class FutureHelper {
    @Autowired
    private Executor executor;

    /**
     * stream 异步执行并返回 List
     *
     * @param collection 原始数据集
     * @param supplier   分批执行逻辑
     * @param <T>        元素类型
     * @return 结果集
     */
    public <T> List<T> completableList(Collection<T> collection, Supplier<T> supplier) {
        return streamOfSupplier(collection, supplier).collect(Collectors.toList());
    }

    /**
     * stream 异步执行并返回 Set
     *
     * @param collection 原始数据集
     * @param supplier   分批执行逻辑
     * @param <T>        元素类型
     * @return 结果集
     */
    public <T> Set<T> completableSet(Collection<T> collection, Supplier<T> supplier) {
        return streamOfSupplier(collection, supplier).collect(Collectors.toSet());
    }

    /**
     * stream 异步执行并返回 List
     *
     * @param collection 原始数据集
     * @param mapper     执行逻辑
     * @param <T>        元素类型
     * @return 结果集
     */
    public <T, R> List<T> completableList(Collection<R> collection, Function<R, T> mapper) {
        return streamOfFunction(collection, mapper).collect(Collectors.toList());
    }

    /**
     * stream 异步执行并返回 Set
     *
     * @param collection 原始数据集
     * @param mapper     执行逻辑
     * @param <T>        元素类型
     * @return 结果集
     */
    public <T, R> Set<T> completableSet(Collection<R> collection, Function<R, T> mapper) {
        return streamOfFunction(collection, mapper).collect(Collectors.toSet());
    }

    /**
     * stream + 异步 分批处理 List
     *
     * @param list     原始数据集
     * @param size     每批大小
     * @param consumer 处理逻辑
     * @param <T>      元素类型
     * @return 结果集
     */
    public <T, U> List<T> batchForList(List<U> list, int size, BiConsumer<List<T>, List<U>> consumer) {
        return batchForStream(list, size, new ArrayList<>(), consumer);
    }

    /**
     * stream + 异步 分批处理 Map
     *
     * @param list     原始数据集
     * @param size     每批大小
     * @param consumer 处理逻辑
     * @param <T>      元素类型
     * @return 结果集
     */
    public <T, K, V> Map<K, V> batchForMap(List<T> list, int size, BiConsumer<Map<K, V>, List<T>> consumer) {
        return batchForStream(list, size, new HashMap<>(), consumer);
    }

    /**
     * stream + 异步 分批处理
     *
     * @param list     原始数据集
     * @param size     每批大小
     * @param consumer 处理逻辑
     * @param <T>      元素类型
     * @return 结果集
     */
    public <T, R> R batchForStream(List<T> list, int size, R r, BiConsumer<R, List<T>> consumer) {
        List<List<T>> partedList = Lists.partition(list, size);
        CompletableFuture.allOf(
            partedList
                .stream()
                .map(subList -> {
                    Runnable runnable = () -> consumer.accept(r, subList);
                    return CompletableFuture.runAsync(TtlRunnable.get(runnable), executor);
                })
                .toArray(CompletableFuture[]::new)
        ).join();
        return r;
    }

    /**
     * stream 异步执行并返回 Stream
     *
     * @param collection 原始数据集
     * @param mapper     执行逻辑
     * @param <T>        元素类型
     * @return 结果集
     */
    private <T, R> Stream<T> streamOfFunction(Collection<R> collection, Function<R, T> mapper) {
        List<CompletableFuture<T>> futureList = collection
            .stream()
            .map(item -> CompletableFuture.supplyAsync(() -> mapper.apply(item), executor))
            .collect(Collectors.toList());
        return futureList.stream().map(CompletableFuture::join).filter(Objects::nonNull);
    }

    /**
     * stream 异步执行并返回 Stream
     *
     * @param collection 原始数据集
     * @param supplier   分批执行逻辑
     * @param <T>        元素类型
     * @return 结果集
     */
    private <T> Stream<T> streamOfSupplier(Collection<T> collection, Supplier<T> supplier) {
        List<CompletableFuture<T>> futureList = collection
            .stream()
            .map(item -> CompletableFuture.supplyAsync(supplier, executor))
            .collect(Collectors.toList());
        return futureList.stream().map(CompletableFuture::join).filter(Objects::nonNull);
    }
}
