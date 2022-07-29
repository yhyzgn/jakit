package com.yhy.jakit.starter.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created on 2022-07-26 17:33
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class RedisHelper {
    @Autowired
    private JsonHelper jsonHelper;
    @Autowired(required = false)
    private List<Listener> listeners;

    /**
     * 保存一个对象
     *
     * @param key 键名
     * @param t   值
     * @param <T> 数据类型
     */
    public <T> void set(@NonNull String key, @NonNull T t) {
        set(key, t, original -> original);
    }

    /**
     * 保存一个对象
     *
     * @param key    键名
     * @param t      值
     * @param action key 前缀操作
     * @param <T>    数据类型
     */
    public <T> void set(@NonNull String key, @NonNull T t, KeyPrefixAction action) {
        set(key, jsonHelper.toJson(t), action);
    }

    /**
     * 保存一个对象
     *
     * @param key     键名
     * @param t       值
     * @param timeout 有效期
     * @param <T>     数据类型
     */
    public <T> void set(@NonNull String key, @NonNull T t, @NonNull Duration timeout) {
        set(key, t, timeout, original -> original);
    }

    /**
     * 保存一个对象
     *
     * @param key     键名
     * @param t       值
     * @param timeout 有效期
     * @param action  key 前缀操作
     * @param <T>     数据类型
     */
    public <T> void set(@NonNull String key, @NonNull T t, @NonNull Duration timeout, KeyPrefixAction action) {
        set(key, jsonHelper.toJson(t), timeout, action);
    }

    /**
     * 保存一个字符串
     *
     * @param key   键名
     * @param value 值
     */
    public void set(@NonNull String key, @NonNull String value) {
        set(key, value, original -> original);
    }

    /**
     * 保存一个字符串
     *
     * @param key    键名
     * @param value  值
     * @param action key 前缀操作
     */
    public void set(@NonNull String key, @NonNull String value, KeyPrefixAction action) {
        String redisKey = withPrefix(key, action);
        String original = getAsString(key, action);
        template().opsForValue().set(redisKey, value);
        // 处理监听事件
        if (null != listeners) {
            listeners.forEach(lt -> {
                if (null == original) {
                    lt.set(redisKey, value, null);
                } else {
                    lt.update(redisKey, original, value, null);
                }
            });
        }
    }

    /**
     * 保存一个字符串
     *
     * @param key     键名
     * @param value   值
     * @param timeout 有效期
     */
    public void set(@NonNull String key, @NonNull String value, @NonNull Duration timeout) {
        set(key, value, timeout, original -> original);
    }

    /**
     * 保存一个字符串
     *
     * @param key     键名
     * @param value   值
     * @param timeout 有效期
     * @param action  key 前缀操作
     */
    public void set(@NonNull String key, @NonNull String value, @NonNull Duration timeout, KeyPrefixAction action) {
        String redisKey = withPrefix(key, action);
        String original = getAsString(key, action);
        template().opsForValue().set(redisKey, value, timeout);
        // 处理监听事件
        if (null != listeners) {
            listeners.forEach(lt -> {
                if (null == original) {
                    lt.set(redisKey, value, timeout);
                } else {
                    lt.update(redisKey, original, value, timeout);
                }
            });
        }
    }

    /**
     * 更新一个值
     *
     * @param key   键名
     * @param value 新值
     * @param <T>   数据类型
     */
    public <T> void update(@NonNull String key, @NonNull T value) {
        update(key, value, original -> original);
    }

    /**
     * 更新一个值
     *
     * @param key    键名
     * @param value  新值
     * @param action key 前缀操作
     * @param <T>    数据类型
     */
    public <T> void update(@NonNull String key, @NonNull T value, KeyPrefixAction action) {
        long expire = getExpire(key, action);
        if (expire > -1) {
            set(key, value, Duration.ofSeconds(expire));
            return;
        }
        set(key, value);
    }

    /**
     * 更新一个值
     *
     * @param key   键名
     * @param value 新值
     */
    public void update(@NonNull String key, @NonNull String value) {
        update(key, value, original -> original);
    }

    /**
     * 更新一个值
     *
     * @param key    键名
     * @param value  新值
     * @param action key 前缀操作
     */
    public void update(@NonNull String key, @NonNull String value, KeyPrefixAction action) {
        long expire = getExpire(key, action);
        if (expire > -1) {
            set(key, value, Duration.ofSeconds(expire));
            return;
        }
        set(key, value);
    }

    /**
     * 获取一个值
     *
     * @param key   键名
     * @param clazz 类型
     * @param <T>   结果的实际类型
     * @return 值
     */
    @Nullable
    public <T> T get(@NonNull String key, @NonNull Class<T> clazz) {
        return get(key, clazz, original -> original);
    }

    /**
     * 获取一个值
     *
     * @param key    键名
     * @param clazz  类型
     * @param action key 前缀操作
     * @param <T>    结果的实际类型
     * @return 值
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(@NonNull String key, @NonNull Class<T> clazz, KeyPrefixAction action) {
        String value = getAsString(key, action);
        if (clazz == String.class) {
            return (T) value;
        }
        try {
            return jsonHelper.fromJson(value, clazz);
        } finally {
            if (null != listeners) {
                listeners.forEach(lt -> {
                    lt.get(withPrefix(key, action), value);
                });
            }
        }
    }

    /**
     * 获取一个值
     *
     * @param key  键名
     * @param type 复杂类型的 type
     * @param <T>  结果的实际类型
     * @return 值
     */
    @Nullable
    public <T> T get(@NonNull String key, @NonNull TypeReference<T> type) {
        return get(key, type, original -> original);
    }

    /**
     * 获取一个值
     *
     * @param key    键名
     * @param type   复杂类型的 type
     * @param action key 前缀操作
     * @param <T>    结果的实际类型
     * @return 值
     */
    @Nullable
    public <T> T get(@NonNull String key, @NonNull TypeReference<T> type, KeyPrefixAction action) {
        String value = getAsString(key, action);
        try {
            return jsonHelper.fromJson(value, type);
        } finally {
            if (null != listeners) {
                listeners.forEach(lt -> {
                    lt.get(withPrefix(key, action), value);
                });
            }
        }
    }

    /**
     * 获取字符串
     *
     * @param key 键名
     * @return 值
     */
    public String getString(@NonNull String key) {
        return getString(key, original -> original);
    }

    /**
     * 获取字符串
     *
     * @param key    键名
     * @param action key 前缀操作
     * @return 值
     */
    public String getString(@NonNull String key, KeyPrefixAction action) {
        final String value = getAsString(key, action);

        if (null != listeners) {
            listeners.forEach(lt -> lt.get(withPrefix(key, action), value));
        }

        return value;
    }

    /**
     * 获取剩余过期时间
     *
     * @param key 键名
     * @return 过期时间
     */
    public long getExpire(@NonNull String key) {
        return getExpire(key, original -> original);
    }

    /**
     * 获取剩余过期时间
     *
     * @param key    键名
     * @param action key 前缀操作
     * @return 过期时间
     */
    public long getExpire(@NonNull String key, KeyPrefixAction action) {
        return Optional.ofNullable(template().getExpire(withPrefix(key, action), TimeUnit.SECONDS)).orElse(-1L);
    }

    /**
     * 按 key 前缀批量删除
     *
     * @param pattern key 前缀
     * @return 是否删除成功
     */
    public boolean deleteByPattern(@NonNull String pattern) {
        Set<String> keys = keysWithoutPrefix(pattern, original -> original);
        return delete(keys);
    }

    /**
     * 按 key 前缀批量删除
     *
     * @param pattern pattern pattern
     * @param action  key key操作
     * @return 是否删除成功
     */
    public boolean deleteByPattern(@NonNull String pattern, KeyPrefixAction action) {
        Set<String> keys = keysWithoutPrefix(pattern, action);
        return delete(keys);
    }

    /**
     * 删除
     *
     * @param keys 键名
     * @return 是否删除成功
     */
    public boolean delete(@NonNull Collection<String> keys) {
        return delete(original -> original, keys);
    }

    /**
     * 删除
     *
     * @param action key 前缀操作
     * @param keys   键名
     * @return 是否删除成功
     */
    public boolean delete(KeyPrefixAction action, @NonNull Collection<String> keys) {
        String[] arr = keys.toArray(new String[0]);
        return delete(action, arr);
    }

    /**
     * 删除
     *
     * @param keys 键名
     * @return 是否删除成功
     */
    public boolean delete(@NonNull String... keys) {
        return delete(original -> original, keys);
    }

    /**
     * 删除
     *
     * @param action key 前缀操作
     * @param keys   键名
     * @return 是否删除成功
     */
    public boolean delete(KeyPrefixAction action, @NonNull String... keys) {
        if (keys.length == 0) {
            return false;
        }
        List<String> redisKeys = Arrays.stream(keys).map(item -> withPrefix(item, action)).collect(Collectors.toList());
        Long result = template().delete(redisKeys);
        boolean successful = null != result && result == keys.length;
        if (null != listeners) {
            listeners.forEach(lt -> {
                lt.delete(redisKeys, successful);
            });
        }
        return successful;
    }

    /**
     * 查询 key 是否存在
     *
     * @param key 键名
     * @return 是否存在
     */
    public boolean exists(@NonNull String key) {
        return exists(key, original -> original);
    }

    /**
     * 查询 key 是否存在
     *
     * @param key    键名
     * @param action key 前缀操作
     * @return 是否存在
     */
    public boolean exists(@NonNull String key, KeyPrefixAction action) {
        Boolean result = template().hasKey(withPrefix(key, action));
        return null != result && result;
    }

    /**
     * 设置过期时间
     *
     * @param key     键名
     * @param timeout 过期时间
     * @return 是否设置成功
     */
    public boolean expire(@NonNull String key, @NonNull Duration timeout) {
        return expire(key, timeout, original -> original);
    }

    /**
     * 设置过期时间
     *
     * @param key     键名
     * @param timeout 过期时间
     * @param action  key 前缀操作
     * @return 是否设置成功
     */
    public boolean expire(@NonNull String key, @NonNull Duration timeout, KeyPrefixAction action) {
        String redisKey = withPrefix(key, action);
        Boolean result = template().boundValueOps(redisKey).expire(timeout);
        boolean successful = null != result && result;
        if (null != listeners) {
            listeners.forEach(lt -> {
                lt.expire(redisKey, timeout, successful);
            });
        }
        return successful;
    }

    /**
     * 给 key 重命名
     *
     * @param originalKey 旧的键名
     * @param newlyKey    新的键名
     */
    public void rename(String originalKey, String newlyKey) {
        rename(originalKey, newlyKey, original -> original);
    }

    /**
     * 给 key 重命名
     *
     * @param originalKey 旧的键名
     * @param newlyKey    新的键名
     * @param action      key 前缀操作
     */
    public void rename(String originalKey, String newlyKey, KeyPrefixAction action) {
        String ognKey = withPrefix(originalKey, action);
        String nwlKey = withPrefix(newlyKey, action);
        template().boundValueOps(ognKey).rename(nwlKey);
        if (null != listeners) {
            listeners.forEach(lt -> {
                lt.rename(ognKey, nwlKey);
            });
        }
    }

    /**
     * 消息发布
     *
     * @param channel 消息通道
     * @param message 消息
     * @param <T>     数据类型
     */
    public <T> void publish(String channel, T message) {
        publish(channel, message, original -> original);
    }

    /**
     * 消息发布
     *
     * @param channel 消息通道
     * @param message 消息
     * @param action  前缀改造器
     * @param <T>     数据类型
     */
    public <T> void publish(String channel, T message, KeyPrefixAction action) {
        String redisKey = withPrefix(channel, action);
        String value = message instanceof String ? (String) message : jsonHelper.toJson(message);
        template().convertAndSend(redisKey, value);
        if (null != listeners) {
            listeners.forEach(lt -> {
                lt.publish(redisKey, value);
            });
        }
    }

    /**
     * 执行 lua 脚本
     *
     * @param script lua 脚本
     * @param keys   keys
     * @param args   参数
     * @param <T>    返回值类型
     * @return 结果
     */
    public <T> T script(RedisScript<T> script, List<String> keys, Object... args) {
        return template().execute(script, keys, args);
    }

    /**
     * 管道操作
     * <p>
     * 使用 {@link RedisSerializer#string()} 序列化结果
     *
     * @param action 具体操作
     * @return 操作结果
     */
    public List<Object> pipelined(final RedisCallback<?> action) {
        return pipelined(action, null);
    }

    /**
     * 管道操作
     *
     * @param action           具体操作
     * @param resultSerializer 结果序列化，默认使用 {@link RedisSerializer#string()}
     * @return 操作结果
     */
    public List<Object> pipelined(final RedisCallback<?> action, RedisSerializer<?> resultSerializer) {
        if (null == resultSerializer) {
            resultSerializer = RedisSerializer.string();
        }
        return template().executePipelined(action, resultSerializer);
    }

    /**
     * 批量查询 key
     *
     * @param pattern key 格式
     * @return keys
     */
    public Set<String> keys(String pattern) {
        return keys(pattern, original -> original);
    }

    /**
     * 批量查询 key
     *
     * @param pattern key 格式
     * @param action  改造器
     * @return keys
     */
    public Set<String> keys(String pattern, KeyPrefixAction action) {
        return template().keys(withPrefix(pattern, action));
    }

    /**
     * 批量查询 key
     * <p>
     * 返回值带前缀
     *
     * @param pattern key 格式
     * @return keys
     */
    public Set<String> keysWithoutPrefix(String pattern) {
        return keysWithoutPrefix(pattern, original -> original);
    }

    /**
     * 批量查询 key
     * <p>
     * 返回值带前缀
     *
     * @param pattern key 格式
     * @param action  改造器
     * @return keys
     */
    public Set<String> keysWithoutPrefix(String pattern, KeyPrefixAction action) {
        Set<String> keys = keys(withPrefix(pattern, action));
        return keys.stream().map(key -> key.replace(keyPrefix() + ":", "")).collect(Collectors.toSet());
    }

    /**
     * 获取值为字符串
     *
     * @param key 键名
     * @return 值
     */
    private String getAsString(String key, KeyPrefixAction action) {
        return template().opsForValue().get(withPrefix(key, action));
    }

    /**
     * 给 key 添加前缀
     *
     * @param key 原始 key
     * @return 带前缀的 key
     */
    private String withPrefix(String key, KeyPrefixAction action) {
        return withPrefix(keyPrefix(), key, action);
    }

    /**
     * 获取当前 StringRedisTemplate 实例
     *
     * @param dynamicTemplate 动态数据源
     * @return 当前 StringRedisTemplate 实例
     */
    // @NotNull
    // public static StringRedisTemplate currentTemplate(DynamicStringRedisTemplate dynamicTemplate) {
    //     StringRedisTemplate template = dynamicTemplate.current();
    //     Assert.notNull(template, "Illegal instance of StringRedisTemplate got from DynamicStringRedisTemplate.");
    //     return template;
    // }

    /**
     * 拼接 redis key 前缀
     *
     * @param keyPrefix redis 前缀
     * @param key       key
     * @param action    key 前缀操作
     * @return 最终 key
     */
    public static String withPrefix(String keyPrefix, String key, KeyPrefixAction action) {
        String prefix = StringUtils.hasText(keyPrefix) ? keyPrefix : "";
        // 改造器
        if (null != action) {
            prefix = action.apply(prefix);
        }
        if (!StringUtils.hasText(prefix) || key.startsWith(prefix)) {
            return key;
        }
        if (!prefix.endsWith(":") && !key.startsWith(":")) {
            key = ":" + key;
        }
        return prefix + key;
    }

    public abstract StringRedisTemplate template();

    public abstract String keyPrefix();

    /**
     * key 前缀操作器
     * <p>
     * 一般跨项目操作时会用到
     */
    @FunctionalInterface
    public interface KeyPrefixAction {

        /**
         * 处理 key 前缀
         *
         * @param original 原始前缀
         * @return 改造后的前缀
         */
        String apply(String original);
    }

    /**
     * redis 操作事件监听
     */
    public interface Listener {
        /**
         * 设置值
         *
         * @param key     键名
         * @param value   值
         * @param timeout 过期时间
         */
        void set(String key, String value, Duration timeout);

        /**
         * 更新值
         *
         * @param key      键名
         * @param original 旧值
         * @param newly    新值
         * @param timeout  过期时间
         */
        void update(String key, String original, String newly, Duration timeout);

        /**
         * 获取值
         *
         * @param key   键名
         * @param value 值
         */
        void get(String key, String value);

        /**
         * 删除键
         *
         * @param keys       键名
         * @param successful 是否删除成功
         */
        void delete(List<String> keys, boolean successful);

        /**
         * 给键设置过期时间
         *
         * @param key        键名
         * @param timeout    过期时间
         * @param successful 是否设置成功
         */
        void expire(String key, Duration timeout, boolean successful);

        /**
         * 给键重命名
         *
         * @param original 旧键名
         * @param newly    新键名
         */
        void rename(String original, String newly);

        /**
         * 消息发布
         *
         * @param channel 消息通道
         * @param message 消息
         */
        void publish(String channel, String message);
    }

    /**
     * 自定义 redis 异常
     */
    public static class RedisException extends RuntimeException {
        private static final long serialVersionUID = 813924096566825849L;

        /**
         * redis 异常
         *
         * @param msg 异常信息
         */
        public RedisException(String msg) {
            super(msg);
        }
    }
}
