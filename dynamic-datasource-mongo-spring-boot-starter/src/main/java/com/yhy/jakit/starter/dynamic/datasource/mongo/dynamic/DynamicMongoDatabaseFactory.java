package com.yhy.jakit.starter.dynamic.datasource.mongo.dynamic;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.yhy.jakit.starter.dynamic.datasource.mongo.config.MongoDBProperties;
import com.yhy.jakit.starter.dynamic.datasource.mongo.constant.MongoConstant;
import com.yhy.jakit.starter.dynamic.datasource.mongo.holder.MongoNameHolder;
import com.yhy.jakit.util.internal.Lists;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.MongoPropertiesClientSettingsBuilderCustomizer;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2021-12-06 17:53
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class DynamicMongoDatabaseFactory {
    private final Map<String, MongoDatabaseFactory> factoryMap = new ConcurrentHashMap<>();
    private final Map<String, MongoClient> clientMap = new ConcurrentHashMap<>();

    private final Environment environment;

    /**
     * Constructor
     *
     * @param defaultFactory The default {@link MongoDatabaseFactory} which created by Spring IOC.
     * @param environment    The {@link Environment} current.
     */
    public DynamicMongoDatabaseFactory(MongoDatabaseFactory defaultFactory, Environment environment) {
        factoryMap.put(MongoConstant.DEFAULT_MONGO_NAME, defaultFactory);
        this.environment = environment;
        MongoNameHolder.set(MongoConstant.DEFAULT_MONGO_NAME);
    }

    /**
     * Get current {@link MongoDatabaseFactory} for current thread.
     * <p>
     * Call this method to get current {@link MongoDatabaseFactory} for current thread exactly.
     *
     * @return Current {@link MongoDatabaseFactory} instance.
     */
    public MongoDatabaseFactory current() {
        return factoryMap.get(MongoNameHolder.get());
    }

    /**
     * Get name of current {@link MongoDatabaseFactory} for current thread.
     * <p>
     * Call this method to get name of current {@link MongoDatabaseFactory} for current thread exactly.
     *
     * @return Current name of {@link MongoDatabaseFactory} instance.
     */
    public String currentName() {
        return MongoNameHolder.get();
    }

    /**
     * Add a {@link MongoDatabaseFactory} with custom {@link MongoDBProperties} dynamically.
     *
     * @param properties Custom {@link MongoDBProperties}
     * @param override   Override if {@link MongoDatabaseFactory} exists.
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean add(MongoDBProperties properties, boolean override) {
        if (factoryMap.containsKey(properties.getName()) && !override) {
            return true;
        }
        MongoDatabaseFactory factory = createFactory(properties.getProperties());
        factoryMap.put(properties.getName(), factory);
        return true;
    }

    /**
     * Add a {@link MongoDatabaseFactory} with custom {@link MongoDBProperties} dynamically.
     * And then switch to this {@link MongoDatabaseFactory} automatically if addition success.
     *
     * @param properties Custom {@link MongoDBProperties}
     * @param override   Override if {@link MongoDatabaseFactory} exists.
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean addAndSwitchTo(MongoDBProperties properties, boolean override) {
        if (add(properties, override)) {
            MongoNameHolder.set(properties.getName());
        }
        return true;
    }

    /**
     * Switch to any {@link MongoDatabaseFactory} added manually.
     *
     * @param mongoName The name of mongo datasource which you want to switch to.
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean switchTo(String mongoName) {
        if (!factoryMap.containsKey(mongoName)) {
            return false;
        }
        MongoNameHolder.set(mongoName);
        return true;
    }

    /**
     * Delete a {@link MongoDatabaseFactory} manually
     *
     * @param mongoName The name of mongo datasource which you want to switch to.
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean delete(String mongoName) {
        if (!factoryMap.containsKey(mongoName)) {
            return false;
        }
        factoryMap.remove(mongoName);
        return true;
    }

    /**
     * Get the default {@link MongoDatabaseFactory} bean created by Spring IOC.
     *
     * @return The default {@link MongoDatabaseFactory} bean.
     */
    public MongoDatabaseFactory getDefault() {
        return factoryMap.get(MongoConstant.DEFAULT_MONGO_NAME);
    }

    /**
     * Switch to default {@link MongoDatabaseFactory}.
     */
    public void switchToDefault() {
        MongoNameHolder.set(MongoConstant.DEFAULT_MONGO_NAME);
    }

    /**
     * Call this method to remove current mongo name in ThreadLocal.
     */
    public void destroy() {
        MongoNameHolder.clear();
    }

    public MongoClient mongoClient() {
        if (clientMap.containsKey(MongoNameHolder.get())) {
            return clientMap.get(MongoNameHolder.get());
        }
        throw new IllegalStateException("MongoClient instance is not exists for current holder.");
    }

    /**
     * Create a {@link MongoDatabaseFactory} instance by manual.
     *
     * @param properties Custom {@link MongoDBProperties}
     * @return factory instance
     */
    private MongoDatabaseFactory createFactory(MongoProperties properties) {
        if (StringUtils.hasText(properties.getUri())) {
            return createByUri(properties.getUri());
        }
        return createByProperties(properties);
    }

    private SimpleMongoClientDatabaseFactory createByUri(String uri) {
        return new SimpleMongoClientDatabaseFactory(uri);
    }

    private SimpleMongoClientDatabaseFactory createByProperties(MongoProperties properties) {
        MongoPropertiesClientSettingsBuilderCustomizer customizer = new MongoPropertiesClientSettingsBuilderCustomizer(properties, environment);
        MongoClientFactory clientFactory = new MongoClientFactory(Lists.of(customizer));
        MongoClient client = clientFactory.createMongoClient(MongoClientSettings.builder().build());
        clientMap.put(MongoNameHolder.get(), client);
        return new SimpleMongoClientDatabaseFactory(client, properties.getDatabase());
    }
}
