package com.yhy.jakit.starter.dynamic.datasource.mybatis.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created on 2022-06-14 14:30
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class DynamicDataSourceProperties {

    @Autowired(required = false)
    private DataSourceProperties properties;

    private boolean initEnabled;

    private List<String> initSqlFileList;
}
