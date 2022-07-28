package com.yhy.jakit.starter.logging.mybatis;

import com.yhy.jakit.starter.logging.LoggingSpringAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created on 2021-12-09 15:47
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ComponentScan
@Import(LoggingSpringAutoConfiguration.class)
public class LoggingMybatisAutoConfiguration {
}
