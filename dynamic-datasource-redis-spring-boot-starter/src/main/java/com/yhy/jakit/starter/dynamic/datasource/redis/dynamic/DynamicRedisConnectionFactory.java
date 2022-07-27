package com.yhy.jakit.starter.dynamic.datasource.redis.dynamic;

import com.yhy.jakit.starter.dynamic.datasource.redis.config.RedisConnectionConfiguration;
import com.yhy.jakit.starter.dynamic.datasource.redis.constant.RedisConstant;
import com.yhy.jakit.starter.dynamic.datasource.redis.holder.RedisNameHolder;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dynamic {@link RedisConnectionFactory}
 * <p>
 * <a href="https://iogogogo.github.io/2020/01/10/spring-boot-redis-multi-instance/">Reference</a>
 * <p>
 * Created on 2021-05-25 14:32
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public class DynamicRedisConnectionFactory extends RedisConnectionConfiguration {
    private final Map<String, RedisConnectionFactory> factoryMap = new ConcurrentHashMap<>();

    /**
     * Constructor
     *
     * @param defaultFactory The default {@link RedisConnectionFactory} which created by Spring IOC.
     */
    public DynamicRedisConnectionFactory(RedisConnectionFactory defaultFactory) {
        factoryMap.put(RedisConstant.DEFAULT_REDIS_NAME, defaultFactory);
        RedisNameHolder.set(RedisConstant.DEFAULT_REDIS_NAME);
    }

    /**
     * Get current {@link RedisConnectionFactory} for current thread.
     * <p>
     * Call this method to get current {@link RedisConnectionFactory} for current thread exactly.
     *
     * @return Current {@link RedisConnectionFactory} instance.
     */
    public RedisConnectionFactory current() {
        return factoryMap.get(RedisNameHolder.get());
    }

    /**
     * Get name of current {@link RedisConnectionFactory} for current thread.
     * <p>
     * Call this method to get name of current {@link RedisConnectionFactory} for current thread exactly.
     *
     * @return Current name of {@link RedisConnectionFactory} instance.
     */
    public String currentName() {
        return RedisNameHolder.get();
    }

    /**
     * Add a {@link RedisConnectionFactory} with custom {@link RedisProperties} dynamically.
     * <p>
     * By default, clientType is {@link RedisProperties.ClientType#LETTUCE}, timeout is 6s
     *
     * @param properties Custom {@link RedisProperties}
     * @param override   Override if {@link RedisConnectionFactory} exists.
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean add(RedisProperties properties, boolean override) {
        if (factoryMap.containsKey(properties.getClientName()) && !override) {
            return true;
        }
        if (null == properties.getTimeout()) {
            properties.setTimeout(Duration.ofSeconds(6));
        }
        if (null == properties.getClientType()) {
            properties.setClientType(RedisProperties.ClientType.LETTUCE);
        }
        RedisConnectionFactory factory = createFactory(properties);
        factoryMap.put(properties.getClientName(), factory);
        return true;
    }

    /**
     * Add a {@link RedisConnectionFactory} with custom {@link RedisProperties} dynamically.
     * And then switch to this {@link RedisConnectionFactory} automatically if addition success.
     *
     * @param properties Custom {@link RedisProperties}
     * @param override   Override if {@link RedisConnectionFactory} exists.
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean addAndSwitchTo(RedisProperties properties, boolean override) {
        if (add(properties, override)) {
            RedisNameHolder.set(properties.getClientName());
        }
        return true;
    }

    /**
     * Switch to any {@link RedisConnectionFactory} added manually.
     *
     * @param redisName The name of redis datasource which you want to switch to.
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean switchTo(String redisName) {
        if (!factoryMap.containsKey(redisName)) {
            return false;
        }
        RedisNameHolder.set(redisName);
        return true;
    }

    /**
     * Delete a {@link RedisConnectionFactory} manually
     *
     * @param redisName The name of redis datasource which you want to switch to.
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean delete(String redisName) {
        if (!factoryMap.containsKey(redisName)) {
            return false;
        }
        factoryMap.remove(redisName);
        return true;
    }

    /**
     * Get the default {@link RedisConnectionFactory} bean created by Spring IOC.
     *
     * @return The default {@link RedisConnectionFactory} bean.
     */
    public RedisConnectionFactory getDefault() {
        return factoryMap.get(RedisConstant.DEFAULT_REDIS_NAME);
    }

    /**
     * Switch to default {@link RedisConnectionFactory}.
     */
    public void switchToDefault() {
        RedisNameHolder.set(RedisConstant.DEFAULT_REDIS_NAME);
    }

    /**
     * Call this method to remove current redis name in ThreadLocal.
     */
    public void destroy() {
        RedisNameHolder.clear();
    }

    /**
     * Create a {@link RedisConnectionFactory} instance by manual.
     *
     * @param properties Custom {@link RedisProperties}
     * @return {@link LettuceConnectionFactory} or {@link JedisConnectionFactory} instance, judged by the property 'client-type' of @{@link RedisProperties}
     */
    private RedisConnectionFactory createFactory(RedisProperties properties) {
        if (properties.getClientType() == RedisProperties.ClientType.JEDIS) {
            JedisConnectionFactory factory = jedisConnectionFactory(properties);
            factory.afterPropertiesSet();
            return factory;
        }
        LettuceConnectionFactory factory = lettuceConnectionFactory(properties);
        factory.afterPropertiesSet();
        return factory;
    }

    /**
     * Create a {@link LettuceConnectionFactory} instance.
     *
     * @param properties Custom {@link RedisProperties}
     * @return New {@link LettuceConnectionFactory} instance
     */
    private LettuceConnectionFactory lettuceConnectionFactory(RedisProperties properties) {
        GenericObjectPoolConfig poolConfig = createPoolConfig(properties);

        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
        builder.poolConfig(poolConfig);
        builder.commandTimeout(properties.getTimeout());
        LettucePoolingClientConfiguration configuration = builder.build();

        RedisSentinelConfiguration sentinelConfig = getSentinelConfig(properties);
        if (null != sentinelConfig) {
            return new LettuceConnectionFactory(sentinelConfig, configuration);
        }
        RedisClusterConfiguration clusterConfig = getClusterConfiguration(properties);
        if (null != clusterConfig) {
            return new LettuceConnectionFactory(clusterConfig, configuration);
        }
        return new LettuceConnectionFactory(getStandaloneConfig(properties), configuration);
    }

    /**
     * Create a {@link JedisConnectionFactory} instance.
     *
     * @param properties Custom {@link RedisProperties}
     * @return New {@link JedisConnectionFactory} instance
     */
    private JedisConnectionFactory jedisConnectionFactory(RedisProperties properties) {
        GenericObjectPoolConfig poolConfig = createPoolConfig(properties);

        JedisClientConfiguration.DefaultJedisClientConfigurationBuilder builder = (JedisClientConfiguration.DefaultJedisClientConfigurationBuilder) JedisClientConfiguration.builder();
        builder.connectTimeout(properties.getTimeout());
        builder.usePooling();
        builder.poolConfig(poolConfig);
        JedisClientConfiguration configuration = builder.build();

        RedisSentinelConfiguration sentinelConfig = getSentinelConfig(properties);
        if (null != sentinelConfig) {
            return new JedisConnectionFactory(sentinelConfig, configuration);
        }
        RedisClusterConfiguration clusterConfig = getClusterConfiguration(properties);
        if (null != clusterConfig) {
            return new JedisConnectionFactory(clusterConfig, configuration);
        }
        return new JedisConnectionFactory(getStandaloneConfig(properties), configuration);
    }

    /**
     * Create a {@link GenericObjectPoolConfig} pool instance.
     *
     * @param properties Custom {@link RedisProperties}
     * @return New {@link GenericObjectPoolConfig} pool instance
     */
    private GenericObjectPoolConfig createPoolConfig(RedisProperties properties) {
        RedisProperties.Pool pool = properties.getClientType() == RedisProperties.ClientType.LETTUCE ? properties.getLettuce().getPool() : properties.getJedis().getPool();
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        if (null != pool) {
            poolConfig.setMaxTotal(pool.getMaxActive());
            poolConfig.setMaxWait(pool.getMaxWait());
            poolConfig.setMaxIdle(pool.getMaxIdle());
            poolConfig.setMinIdle(pool.getMinIdle());
        }
        return poolConfig;
    }
}
