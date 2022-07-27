package com.yhy.jakit.starter.dynamic.datasource.mybatis.utils;

import com.yhy.jakit.starter.dynamic.datasource.mybatis.config.DataSourceConfig;
import com.yhy.jakit.starter.dynamic.datasource.mybatis.config.DynamicDataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

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

    public static int createDatabase(String url, String username, String password, String dbName) {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement stmt = connection.createStatement();
            return stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateSchema(DataSourceConfig config, DynamicDataSourceProperties properties) {
        if (!properties.isInitEnabled()) {
            log.info("SQL script init runner is disabled.");
            return;
        }
        if (CollectionUtils.isEmpty(properties.getInitSqlFileList())) {
            log.info("The init sql fileList is empty.");
            return;
        }
        Assert.hasText(config.getUrl(), "url can not be empty");
        Assert.hasText(config.getUsername(), "username can not be empty");
        Assert.hasText(config.getPassword(), "password can not be empty");

        if (!StringUtils.hasText(config.getDriver())) {
            config.setDriver(DEF_DRIVER);
        }

        try {
            Class.forName(config.getDriver());
            Connection conn = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
            ScriptRunner runner = new ScriptRunner(conn);
            // 自动提交
            runner.setAutoCommit(true);
            runner.setFullLineDelimiter(false);
            // 每条命令间的分隔符
            runner.setDelimiter(";");
            runner.setSendFullScript(false);
            runner.setStopOnError(false);
            Resources.setCharset(StandardCharsets.UTF_8);
            for (String sqlFile : properties.getInitSqlFileList()) {
                runner.runScript(Resources.getResourceAsReader(sqlFile));
            }
            conn.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
