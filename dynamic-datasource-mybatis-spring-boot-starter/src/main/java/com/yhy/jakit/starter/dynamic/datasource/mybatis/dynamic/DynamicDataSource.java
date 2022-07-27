package com.yhy.jakit.starter.dynamic.datasource.mybatis.dynamic;

import com.yhy.jakit.starter.dynamic.datasource.mybatis.config.DynamicDataSourceProperties;
import com.yhy.jakit.starter.dynamic.datasource.mybatis.constant.MybatisConstant;
import com.yhy.jakit.starter.dynamic.datasource.mybatis.holder.MybatisNameHolder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2022-06-14 10:02
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private DynamicDataSourceProperties properties;
    private final Map<Object, Object> dataSourceMap = new ConcurrentHashMap<>();
    private final Map<String, DataSourceProperties> dataSourceInfoMap = new ConcurrentHashMap<>();

    public DynamicDataSource(DynamicDataSourceProperties properties) {
        this.properties = properties;
        DataSource defaultDataSource = createDataSource(properties.getProperties());
        dataSourceMap.put(MybatisConstant.DEFAULT_MYBATIS_NAME, defaultDataSource);
        super.setDefaultTargetDataSource(defaultDataSource);
        super.setTargetDataSources(dataSourceMap);
        // 初始时切到默认数据源
        MybatisNameHolder.set(MybatisConstant.DEFAULT_MYBATIS_NAME);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return MybatisNameHolder.get();
    }

    public String currentName() {
        return MybatisNameHolder.get();
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
            MybatisNameHolder.set(properties.getName());
        }
        return true;
    }

    public synchronized boolean switchTo(String dsName) {
        if (!dataSourceMap.containsKey(dsName)) {
            return false;
        }
        MybatisNameHolder.set(dsName);
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
        return (DataSource) dataSourceMap.get(MybatisConstant.DEFAULT_MYBATIS_NAME);
    }

    public void switchToDefault() {
        MybatisNameHolder.set(MybatisConstant.DEFAULT_MYBATIS_NAME);
    }

    public boolean exist(String dsName) {
        return dataSourceMap.containsKey(dsName);
    }

    public void destroy() {
        MybatisNameHolder.clear();
    }

    private DataSource createDataSource(DataSourceProperties properties) {
        return DataSourceBuilder.create()
            .driverClassName(properties.getDriverClassName())
            .url(properties.getUrl())
            .username(properties.getUsername())
            .password(properties.getPassword()).build();
    }

    public DynamicDataSourceProperties getProperties() {
        return properties;
    }
}
