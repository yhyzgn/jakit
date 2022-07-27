package com.yhy.jakit.starter.dynamic.datasource.jpa.dynamic;

import com.yhy.jakit.starter.dynamic.datasource.jpa.constant.JPAConstant;
import com.yhy.jakit.starter.dynamic.datasource.jpa.holder.JPANameHolder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2021-05-26 11:12
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private final Map<Object, Object> dataSourceMap = new ConcurrentHashMap<>();
    private final Map<String, DataSourceProperties> dataSourceInfoMap = new ConcurrentHashMap<>();

    public DynamicDataSource(DataSourceProperties properties) {
        DataSource defaultDataSource = createDataSource(properties);
        dataSourceMap.put(JPAConstant.DEFAULT_JPA_NAME, defaultDataSource);
        super.setDefaultTargetDataSource(defaultDataSource);
        super.setTargetDataSources(dataSourceMap);
        // 初始时切到默认数据源
        JPANameHolder.set(JPAConstant.DEFAULT_JPA_NAME);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return JPANameHolder.get();
    }

    public String currentName() {
        return JPANameHolder.get();
    }

    public synchronized boolean add(DataSourceProperties properties) {
        return add(properties, true);
    }

    public synchronized boolean add(DataSourceProperties properties, boolean override) {
        if (dataSourceMap.containsKey(properties.getName()) && properties.equals(dataSourceInfoMap.get(properties.getName())) && !override) {
            return true;
        }
        DataSource ds = createDataSource(properties);
        dataSourceMap.put(properties.getName(), ds);
        dataSourceInfoMap.put(properties.getName(), properties);
        // must
        super.afterPropertiesSet();
        return true;
    }

    public synchronized boolean addAndSwitchTo(DataSourceProperties properties) {
        return addAndSwitchTo(properties, true);
    }

    public synchronized boolean addAndSwitchTo(DataSourceProperties properties, boolean override) {
        if (add(properties, override)) {
            JPANameHolder.set(properties.getName());
        }
        return true;
    }

    public synchronized boolean switchTo(String dsName) {
        if (!dataSourceMap.containsKey(dsName)) {
            return false;
        }
        JPANameHolder.set(dsName);
        return true;
    }

    public synchronized boolean delete(String dsName) {
        if (!dataSourceMap.containsKey(dsName)) {
            return false;
        }
        dataSourceMap.remove(dsName);
        dataSourceInfoMap.remove(dsName);
        return true;
    }

    public DataSource getDefaultDataSource() {
        return (DataSource) dataSourceMap.get(JPAConstant.DEFAULT_JPA_NAME);
    }

    public void switchToDefault() {
        JPANameHolder.set(JPAConstant.DEFAULT_JPA_NAME);
    }

    public boolean exist(String dsName) {
        return dataSourceMap.containsKey(dsName);
    }

    public void destroy() {
        JPANameHolder.clear();
    }

    private DataSource createDataSource(DataSourceProperties properties) {
        return DataSourceBuilder.create()
                .driverClassName(properties.getDriverClassName())
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword()).build();
    }
}
