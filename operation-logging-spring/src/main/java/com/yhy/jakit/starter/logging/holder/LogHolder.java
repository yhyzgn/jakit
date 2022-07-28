package com.yhy.jakit.starter.logging.holder;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.yhy.jakit.starter.logging.model.Log;

/**
 * 记录日志
 * <p>
 * Created on 2020-11-02 16:57
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class LogHolder {
    private final static ThreadLocal<Log> LOCAL = new TransmittableThreadLocal<>();

    public static Log newLogging() {
        Log lg = LOCAL.get();
        if (null == lg) {
            lg = new Log();
            set(lg);
        }
        return lg;
    }

    public static void set(Log log) {
        LOCAL.set(log);
    }

    public static Log get() {
        return LOCAL.get();
    }

    public static void remove() {
        LOCAL.remove();
    }
}
