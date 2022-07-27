package com.yhy.jakit.starter.dynamic.datasource.redis.dynamic;

import com.yhy.jakit.starter.dynamic.datasource.redis.config.RedisConfig;
import com.yhy.jakit.starter.dynamic.datasource.redis.constant.RedisConstant;
import com.yhy.jakit.starter.dynamic.datasource.redis.holder.RedisNameHolder;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dynamic {@link StringRedisTemplate}
 * <p>
 * Created on 2021-05-25 15:47
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class DynamicStringRedisTemplate {
    private final Map<String, StringRedisTemplate> templateMap = new ConcurrentHashMap<>();
    private final Map<String, RedisConfig> redisConfigMap = new ConcurrentHashMap<>();

    /**
     * Instance of {@link DynamicRedisConnectionFactory}
     */
    private final DynamicRedisConnectionFactory dynamicFactory;

    /**
     * Constructor
     *
     * @param defaultRedisConfig The default {@link RedisConfig} which autoconfigured by application*.yml or application.properties file, created by Spring IOC.
     * @param dynamicFactory     The single instance of {@link DynamicRedisConnectionFactory}.
     * @param defaultTemplate    The default {@link StringRedisTemplate} which created by Spring IOC.
     */
    public DynamicStringRedisTemplate(RedisConfig defaultRedisConfig, DynamicRedisConnectionFactory dynamicFactory, StringRedisTemplate defaultTemplate) {
        this.dynamicFactory = dynamicFactory;

        templateMap.put(RedisConstant.DEFAULT_REDIS_NAME, defaultTemplate);
        redisConfigMap.put(RedisConstant.DEFAULT_REDIS_NAME, defaultRedisConfig);
        RedisNameHolder.set(RedisConstant.DEFAULT_REDIS_NAME);
    }

    /**
     * Get current {@link StringRedisTemplate} for current thread.
     * <p>
     * Call this method to get current {@link StringRedisTemplate} for current thread exactly.
     *
     * @return Current {@link StringRedisTemplate} instance.
     */
    public StringRedisTemplate current() {
        return templateMap.get(RedisNameHolder.get());
    }

    /**
     * Get name of current {@link StringRedisTemplate} for current thread.
     * <p>
     * Call this method to get name of current {@link StringRedisTemplate} for current thread exactly.
     *
     * @return Current name of {@link StringRedisTemplate} instance.
     */
    public String currentName() {
        return RedisNameHolder.get();
    }

    /**
     * Get current KeyPrefix for current {@link StringRedisTemplate}.
     * <p>
     * Call this method to get current KeyPrefix for current {@link StringRedisTemplate} exactly.
     *
     * @return Current KeyPrefix.
     */
    public String currentKeyPrefix() {
        RedisConfig config = redisConfigMap.get(RedisNameHolder.get());
        return null != config ? config.getKeyPrefix() : "";
    }

    /**
     * Add a {@link StringRedisTemplate} with custom {@link RedisConfig} dynamically.
     * <p>
     * By default, override is true.
     *
     * @param config Custom {@link RedisConfig}
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean add(RedisConfig config) {
        return add(config, true);
    }

    /**
     * Add a {@link StringRedisTemplate} with custom {@link RedisConfig} dynamically.
     *
     * @param config   Custom {@link RedisConfig}
     * @param override Override if {@link StringRedisTemplate} exists.
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean add(RedisConfig config, boolean override) {
        RedisProperties properties = config.getProperties();
        if (templateMap.containsKey(properties.getClientName()) && redisConfigMap.containsKey(properties.getClientName()) && !override) {
            return true;
        }
        // 此处添加连接池工厂时需要临时切换数据源，因为添加成功后要用对应的连接池工厂来创建 StringRedisTemplate
        // 否则 RedisConnectionFactory 很可能是默认数据源
        String lastName = RedisNameHolder.get();
        if (dynamicFactory.addAndSwitchTo(properties, override)) {
            RedisConnectionFactory factory = dynamicFactory.current();
            StringRedisTemplate template = new StringRedisTemplate();
            template.setConnectionFactory(factory);
            template.afterPropertiesSet();
            templateMap.put(properties.getClientName(), template);
            redisConfigMap.put(properties.getClientName(), config);

            // 添加成功后再需要切回去
            RedisNameHolder.set(lastName);
            return true;
        }
        return false;
    }

    /**
     * Add a {@link StringRedisTemplate} with custom {@link RedisConfig} dynamically.
     * And then switch to this {@link StringRedisTemplate} automatically if addition success.
     * <p>
     * By default, override is true.
     *
     * @param config Custom {@link RedisConfig}
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean addAndSwitchTo(RedisConfig config) {
        return addAndSwitchTo(config, true);
    }

    /**
     * Add a {@link StringRedisTemplate} with custom {@link RedisConfig} dynamically.
     * And then switch to this {@link StringRedisTemplate} automatically if addition success.
     *
     * @param config   Custom {@link RedisConfig}
     * @param override Override if {@link StringRedisTemplate} exists.
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean addAndSwitchTo(RedisConfig config, boolean override) {
        if (add(config, override)) {
            RedisNameHolder.set(config.getProperties().getClientName());
            return true;
        }
        return false;
    }

    /**
     * Switch to any {@link StringRedisTemplate} added manually.
     *
     * @param redisName The name of redis datasource which you want to switch to.
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean switchTo(String redisName) {
        if (!templateMap.containsKey(redisName)) {
            return false;
        }
        RedisNameHolder.set(redisName);
        return true;
    }

    /**
     * Delete a {@link StringRedisTemplate} manually
     *
     * @param redisName The name of redis datasource which you want to switch to.
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean delete(String redisName) {
        if (!templateMap.containsKey(redisName)) {
            return false;
        }
        templateMap.remove(redisName);
        redisConfigMap.remove(redisName);
        return true;
    }

    /**
     * Get the default {@link StringRedisTemplate} bean created by Spring IOC.
     *
     * @return The default {@link StringRedisTemplate} bean.
     */
    public StringRedisTemplate getDefault() {
        return templateMap.get(RedisConstant.DEFAULT_REDIS_NAME);
    }

    /**
     * Switch to default {@link StringRedisTemplate}.
     */
    public void switchToDefault() {
        RedisNameHolder.set(RedisConstant.DEFAULT_REDIS_NAME);
    }

    /**
     * Returns true if the dynamic {@link StringRedisTemplate} instance exist, otherwise returns false.
     *
     * @param redisName The name of redis datasource.
     * @return true or false
     */
    public boolean exist(String redisName) {
        return templateMap.containsKey(redisName);
    }

    /**
     * Call this method to remove current redis name in ThreadLocal.
     */
    public void destroy() {
        RedisNameHolder.clear();
    }
}
