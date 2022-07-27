package com.yhy.jakit.starter.dynamic.datasource.jpa.utils;

import com.yhy.jakit.lib.util.constant.Define;
import com.yhy.jakit.lib.util.core.Assert;
import com.yhy.jakit.starter.dynamic.datasource.jpa.config.DataSourceConfig;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.schema.TargetType;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created on 2021-05-26 14:57
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public abstract class DBUtils {
    private final static String DEF_DRIVER = "com.mysql.cj.jdbc.Driver";
    private final static String DEF_DIALECT = "org.hibernate.dialect.MySQL55Dialect";
    // https://www.cnblogs.com/sxdcgaq8080/p/7910474.html
    private final static String DEF_PHYSICAL_STRATEGY = "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy";

    public static int createDatabase(String url, String username, String password, String dbName) {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement stmt = connection.createStatement();
            return stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateSchema(DataSourceConfig config) {
        Assert.notEmpty(config.getUrl(), "url can not be empty");
        Assert.notEmpty(config.getUsername(), "username can not be empty");
        Assert.notEmpty(config.getPassword(), "password can not be empty");

        if (!StringUtils.hasText(config.getDriver())) {
            config.setDriver(DEF_DRIVER);
        }
        if (!StringUtils.hasText(config.getDialect())) {
            config.setDialect(DEF_DIALECT);
        }
        if (!StringUtils.hasText(config.getPhysicalStrategy())) {
            config.setPhysicalStrategy(DEF_PHYSICAL_STRATEGY);
        }

        Map<String, Object> settings = new HashMap<>();
        settings.put(Environment.HBM2DDL_AUTO, "update");
        settings.put(Environment.DIALECT, config.getDialect());
        settings.put(Environment.DRIVER, config.getDriver());
        settings.put(Environment.URL, config.getUrl());
        settings.put(Environment.USER, config.getUsername());
        settings.put(Environment.PASS, config.getPassword());
        settings.put(Environment.PHYSICAL_NAMING_STRATEGY, config.getPhysicalStrategy());
        settings.put(Environment.SHOW_SQL, true);
        settings.put(Environment.AUTOCOMMIT, true);

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(settings).build();
        try {
            MetadataSources ms = new MetadataSources(registry);
            Set<Class<?>> classes = getClassInPackage(Define.BASE_PACKAGE_NAME);
            classes.forEach(ms::addAnnotatedClass);
            MetadataImplementor implementor = (MetadataImplementor) ms.buildMetadata();
            implementor.validate();
            SchemaUpdate su = new SchemaUpdate();
            su.setFormat(true).setHaltOnError(false).setDelimiter(";");
            su.execute(EnumSet.of(TargetType.DATABASE), implementor, registry);
            log.info("Schema updated.");
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    private static Set<Class<?>> getClassInPackage(String packageName) {
        Reflections reflections = new Reflections(packageName, Scanners.TypesAnnotated, Scanners.SubTypes);
        return reflections.getTypesAnnotatedWith(Entity.class);
    }
}
