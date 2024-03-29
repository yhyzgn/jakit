package com.yhy.jakit.starter.tracing.adapter;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.spi.MDCAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created on 2021-08-05 11:46
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class TtlMDCAdapter implements MDCAdapter {

    /**
     * Context map
     */
    protected ThreadLocal<Map<String, String>> threadLocal = new TransmittableThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> childValue(Map<String, String> parentValue) {
            if (parentValue == null) {
                return null;
            }
            return new HashMap<>(parentValue);
        }
    };

    /**
     * Put a context value (the <code>val</code> parameter) as identified with
     * the <code>key</code> parameter into the current thread's context map.
     * Note that contrary to log4j, the <code>val</code> parameter can be null.
     *
     * <p>
     * If the current thread does not have a context map it is created as a side
     * effect of this call.
     *
     * @throws IllegalArgumentException in case the "key" parameter is null
     */
    public void put(String key, String val) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Map<String, String> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<String, String>();
            threadLocal.set(map);
        }
        map.put(key, val);
    }

    /**
     * Get the context identified by the <code>key</code> parameter.
     */
    public String get(String key) {
        Map<String, String> map = threadLocal.get();
        if ((map != null) && (key != null)) {
            return map.get(key);
        } else {
            return null;
        }
    }

    /**
     * Remove the the context identified by the <code>key</code> parameter.
     */
    public void remove(String key) {
        Map<String, String> map = threadLocal.get();
        if (map != null) {
            map.remove(key);
        }
    }

    /**
     * Clear all entries in the MDC.
     */
    public void clear() {
        Map<String, String> map = threadLocal.get();
        if (map != null) {
            map.clear();
            threadLocal.remove();
        }
    }

    /**
     * Returns the keys in the MDC as a {@link Set} of {@link String}s The
     * returned value can be null.
     *
     * @return the keys in the MDC
     */
    public Set<String> getKeys() {
        Map<String, String> map = threadLocal.get();
        if (map != null) {
            return map.keySet();
        } else {
            return null;
        }
    }

    /**
     * Return a copy of the current thread's context map.
     * Returned value may be null.
     */
    public Map<String, String> getCopyOfContextMap() {
        Map<String, String> oldMap = threadLocal.get();
        if (oldMap != null) {
            return new HashMap<String, String>(oldMap);
        } else {
            return null;
        }
    }

    public void setContextMap(Map<String, String> contextMap) {
        threadLocal.set(new HashMap<String, String>(contextMap));
    }
}
