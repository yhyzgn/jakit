package com.yhy.jakit.starter.dynamic.datasource.mongo.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.UuidRepresentation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Created on 2021-12-07 9:42
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Primary
@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb")
@SuppressWarnings("SpringFacetCodeInspection")
public class MongoDBProperties {

    /**
     * Spring redis 配置项
     */
    @Autowired(required = false)
    private MongoProperties properties;

    /**
     * Name of the datasource.
     */
    private String name;

    public MongoDBProperties() {
    }

    private MongoDBProperties(Builder builder) {
        this.name = builder.name;
        this.properties = new MongoProperties();
        BeanUtils.copyProperties(builder, this.properties);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Builder extends MongoProperties {
        private String name;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder host(String host) {
            setHost(host);
            return this;
        }

        public Builder port(int port) {
            setPort(port);
            return this;
        }

        public Builder uri(String uri) {
            setUri(uri);
            return this;
        }

        public Builder database(String database) {
            setDatabase(database);
            return this;
        }

        public Builder authenticationDatabase(String authenticationDatabase) {
            setAuthenticationDatabase(authenticationDatabase);
            return this;
        }

        public Builder username(String username) {
            setUsername(username);
            return this;
        }

        public Builder password(String password) {
            setPassword(password.toCharArray());
            return this;
        }

        public Builder replicaSetName(String replicaSetName) {
            setReplicaSetName(replicaSetName);
            return this;
        }

        public Builder fieldNamingStrategy(Class<?> fieldNamingStrategy) {
            setFieldNamingStrategy(fieldNamingStrategy);
            return this;
        }

        public Builder uuidRepresentation(UuidRepresentation uuidRepresentation) {
            setUuidRepresentation(uuidRepresentation);
            return this;
        }

        public Builder autoIndexCreation(Boolean autoIndexCreation) {
            setAutoIndexCreation(autoIndexCreation);
            return this;
        }

        public MongoDBProperties build() {
            return new MongoDBProperties(this);
        }
    }
}
