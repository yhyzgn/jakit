package com.yhy.jakit.starter.dynamic.datasource.jpa;

import com.yhy.jakit.starter.dynamic.datasource.jpa.dynamic.DynamicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Created on 2021-05-26 11:08
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(DataSourceProperties.class)
@ComponentScan
public class DynamicDataSourceJPAAutoConfiguration {
    @Autowired(required = false)
    private DataSourceProperties properties;

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public DynamicDataSource dynamicDataSource() {
        return new DynamicDataSource(properties);
    }
}
