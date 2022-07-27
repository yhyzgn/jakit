package com.yhy.jakit.starter.dynamic.datasource.mongo.dynamic;

import com.yhy.jakit.starter.dynamic.datasource.mongo.config.MongoDBProperties;
import com.yhy.jakit.starter.dynamic.datasource.mongo.constant.MongoConstant;
import com.yhy.jakit.starter.dynamic.datasource.mongo.holder.MongoNameHolder;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2021-12-07 17:05
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class DynamicMongoTemplate {
    private final Map<String, MongoTemplate> templateMap = new ConcurrentHashMap<>();
    private final Map<String, MongoDBProperties> propertiesMap = new ConcurrentHashMap<>();

    /**
     * Instance of {@link DynamicMongoDatabaseFactory}
     */
    private final DynamicMongoDatabaseFactory dynamicFactory;

    /**
     * Constructor
     *
     * @param properties      The default {@link MongoDBProperties} which autoconfigured by application*.yml or application.properties file, created by Spring IOC.
     * @param dynamicFactory  The single instance of {@link DynamicMongoDatabaseFactory}.
     * @param defaultTemplate The default {@link MongoTemplate} which created by Spring IOC.
     */
    public DynamicMongoTemplate(MongoDBProperties properties, DynamicMongoDatabaseFactory dynamicFactory, MongoTemplate defaultTemplate) {
        this.dynamicFactory = dynamicFactory;

        templateMap.put(MongoConstant.DEFAULT_MONGO_NAME, defaultTemplate);
        propertiesMap.put(MongoConstant.DEFAULT_MONGO_NAME, properties);
        MongoNameHolder.set(MongoConstant.DEFAULT_MONGO_NAME);
    }

    /**
     * Get current {@link MongoTemplate} for current thread.
     * <p>
     * Call this method to get current {@link MongoTemplate} for current thread exactly.
     *
     * @return Current {@link MongoTemplate} instance.
     */
    public MongoTemplate current() {
        return templateMap.get(MongoNameHolder.get());
    }

    /**
     * Get name of current {@link MongoTemplate} for current thread.
     * <p>
     * Call this method to get name of current {@link MongoTemplate} for current thread exactly.
     *
     * @return Current name of {@link MongoTemplate} instance.
     */
    public String currentName() {
        return MongoNameHolder.get();
    }

    /**
     * Add a {@link MongoTemplate} with custom {@link MongoDBProperties} dynamically.
     * <p>
     * By default, override is true.
     *
     * @param properties Custom {@link MongoDBProperties}
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean add(MongoDBProperties properties) {
        return add(properties, true);
    }

    /**
     * Add a {@link MongoTemplate} with custom {@link MongoDBProperties} dynamically.
     *
     * @param properties Custom {@link MongoDBProperties}
     * @param override   Override if {@link MongoTemplate} exists.
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean add(MongoDBProperties properties, boolean override) {
        if (templateMap.containsKey(properties.getName()) && propertiesMap.containsKey(properties.getName()) && !override) {
            return true;
        }
        // 此处添加连接池工厂时需要临时切换数据源，因为添加成功后要用对应的连接池工厂来创建 MongoTemplate
        // 否则 MongoDatabaseFactory 很可能是默认数据源
        String lastName = MongoNameHolder.get();
        if (dynamicFactory.addAndSwitchTo(properties, override)) {
            MongoDatabaseFactory factory = dynamicFactory.current();
            MongoTemplate template = new MongoTemplate(factory);
            templateMap.put(properties.getName(), template);
            propertiesMap.put(properties.getName(), properties);

            // 添加成功后再需要切回去
            MongoNameHolder.set(lastName);
            return true;
        }
        return false;
    }

    /**
     * Add a {@link MongoTemplate} with custom {@link MongoDBProperties} dynamically.
     * And then switch to this {@link MongoTemplate} automatically if addition success.
     * <p>
     * By default, override is true.
     *
     * @param properties Custom {@link MongoDBProperties}
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean addAndSwitchTo(MongoDBProperties properties) {
        return addAndSwitchTo(properties, true);
    }

    /**
     * Add a {@link MongoTemplate} with custom {@link MongoDBProperties} dynamically.
     * And then switch to this {@link MongoTemplate} automatically if addition success.
     *
     * @param properties Custom {@link MongoDBProperties}
     * @param override   Override if {@link MongoTemplate} exists.
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean addAndSwitchTo(MongoDBProperties properties, boolean override) {
        if (add(properties, override)) {
            MongoNameHolder.set(properties.getName());
            return true;
        }
        return false;
    }

    /**
     * Switch to any {@link MongoTemplate} added manually.
     *
     * @param mongoName The name of mongo datasource which you want to switch to.
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean switchTo(String mongoName) {
        if (!templateMap.containsKey(mongoName)) {
            return false;
        }
        MongoNameHolder.set(mongoName);
        return true;
    }

    /**
     * Delete a {@link MongoTemplate} manually
     *
     * @param mongoName The name of mongo datasource which you want to switch to.
     * @return return true while the operation is successful, otherwise return false.
     */
    public synchronized boolean delete(String mongoName) {
        if (!templateMap.containsKey(mongoName)) {
            return false;
        }
        templateMap.remove(mongoName);
        propertiesMap.remove(mongoName);
        return true;
    }

    /**
     * Get the default {@link MongoTemplate} bean created by Spring IOC.
     *
     * @return The default {@link MongoTemplate} bean.
     */
    public MongoTemplate getDefault() {
        return templateMap.get(MongoConstant.DEFAULT_MONGO_NAME);
    }

    /**
     * Switch to default {@link MongoTemplate}.
     */
    public void switchToDefault() {
        MongoNameHolder.set(MongoConstant.DEFAULT_MONGO_NAME);
    }

    /**
     * Returns true if the dynamic {@link MongoTemplate} instance exist, otherwise returns false.
     *
     * @param mongoName The name of mongo datasource.
     * @return true or false
     */
    public boolean exist(String mongoName) {
        return templateMap.containsKey(mongoName);
    }

    /**
     * Call this method to remove current mongo name in ThreadLocal.
     */
    public void destroy() {
        MongoNameHolder.clear();
    }
}
