package com.yhy.jakit.starter.logging;

import com.yhy.jakit.starter.logging.writer.LoggingWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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
public class LoggingSpringAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LoggingWriter loggingWriter() {
        // 默认为控制台打印
        return lg -> log.info("operation-logging [internal]: {}", lg);
    }
}
