package com.yhy.jakit.simple.dynamic.datasource.mybatis.interceptor;

import com.mysql.cj.jdbc.Driver;
import com.yhy.jakit.starter.dynamic.datasource.mybatis.config.DataSourceConfig;
import com.yhy.jakit.starter.dynamic.datasource.mybatis.dynamic.DynamicDataSource;
import com.yhy.jakit.starter.dynamic.datasource.mybatis.utils.DBUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created on 2021-04-21 16:23
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class DataSourceInterceptor implements HandlerInterceptor {
    @Autowired
    private DynamicDataSource dataSource;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*
            所有配置可自己定义来源，比如配置文件，或者配置中心服务
            注意：
                1/2 两步为动态创建数据库和数据表，如果不需要则直接略过即可
         */
        String dsName = request.getHeader("DS");
        if (StringUtils.hasText(dsName)) {
            String dbName = "db_" + dsName;

            // 1、检查并创建数据库
            String dbServerUrl = "jdbc:mysql://localhost:3306/";
            Assert.isTrue(DBUtils.createDatabase(dbServerUrl, "root", "root", dbName) > 0, "数据库创建失败");

            // 2、建库后生成所有的表（mybatis 执行初始化脚本）
            DataSourceConfig config = DataSourceConfig.builder()
                .driver(Driver.class.getCanonicalName())
                .url("jdbc:mysql://localhost:3306/" + dbName + "?useUnicode=true&allowPublicKeyRetrieval=true&characterEncoding=UTF-8&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai")
                .username("root")
                .password("root")
                .build();
            DBUtils.updateSchema(config, dataSource.getProperties());

            ///3、动态数据源
            DataSourceProperties properties = new DataSourceProperties();
            properties.setName(dsName);
            properties.setDriverClassName(Driver.class.getCanonicalName());
            properties.setUrl("jdbc:mysql://localhost:3306/" + dbName + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai");
            properties.setUsername("root");
            properties.setPassword("root");

            // 动态添加并切换
            dataSource.addAndSwitchTo(properties);
        } else {
            dataSource.switchToDefault();
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        dataSource.destroy();
    }
}
