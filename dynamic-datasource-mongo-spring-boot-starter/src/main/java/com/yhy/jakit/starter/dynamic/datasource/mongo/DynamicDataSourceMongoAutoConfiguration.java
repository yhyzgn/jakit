package com.yhy.jakit.starter.dynamic.datasource.mongo;

import com.mongodb.client.MongoClient;
import com.yhy.jakit.starter.dynamic.datasource.mongo.config.MongoDBProperties;
import com.yhy.jakit.starter.dynamic.datasource.mongo.dynamic.DynamicMongoDatabaseFactory;
import com.yhy.jakit.starter.dynamic.datasource.mongo.dynamic.DynamicMongoTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created on 2021-12-06 17:44
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(MongoDatabaseFactory.class)
@AutoConfigureAfter(MongoDatabaseFactory.class)
@ComponentScan
public class DynamicDataSourceMongoAutoConfiguration {
    @Autowired(required = false)
    private Environment environment;
    @Autowired(required = false)
    private MongoDatabaseFactory defaultFactory;
    @Autowired(required = false)
    private MongoTemplate defaultTemplate;
    @Autowired(required = false)
    private MongoDBProperties properties;
    @Autowired(required = false)
    private MongoClient client;

    @Bean
    @ConditionalOnMissingBean
    public DynamicMongoDatabaseFactory dynamicMongoDatabaseFactory() {
        return new DynamicMongoDatabaseFactory(defaultFactory, environment);
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicMongoTemplate dynamicMongoTemplate() {
        return new DynamicMongoTemplate(properties, dynamicMongoDatabaseFactory(), defaultTemplate);
    }
}
