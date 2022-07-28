package com.yhy.jakit.util.internal;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.MDC;

import java.util.Map;

/**
 * 追踪器
 * <p>
 * Created on 2022-07-28 10:28
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Trace {
    private final static ThreadLocal<Map<String, String>> TL = new TransmittableThreadLocal<>();

    static {
        TL.set(Maps.of());
    }

    public static void put(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (null == TL.get()) {
            TL.set(Maps.of());
        }
        TL.get().put(key, value);

        // 同步到 MDC
        MDC.put(key, value);
    }

    public static String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (null == TL.get()) {
            return null;
        }
        return TL.get().get(key);
    }

    public static void clear() {
        // 只清 map 吧
        if (null != TL.get()) {
            TL.get().clear();
        }
        // 也清空 MDC
        MDC.clear();
    }
}
