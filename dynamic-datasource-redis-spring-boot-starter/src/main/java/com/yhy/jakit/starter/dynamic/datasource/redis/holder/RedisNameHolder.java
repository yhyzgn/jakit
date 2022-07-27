package com.yhy.jakit.starter.dynamic.datasource.redis.holder;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * Created on 2021-05-25 15:39
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class RedisNameHolder {
    private final static ThreadLocal<String> TL = new TransmittableThreadLocal<>();

    public static void set(String dsName) {
        TL.set(dsName);
    }

    public static String get() {
        return TL.get();
    }

    public static void clear() {
        TL.remove();
    }
}
