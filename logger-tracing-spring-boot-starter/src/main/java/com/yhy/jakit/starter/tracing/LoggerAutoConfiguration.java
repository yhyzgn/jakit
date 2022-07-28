package com.yhy.jakit.starter.tracing;

import com.yhy.jakit.starter.tracing.application.TracingApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created on 2021-08-05 11:32
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@Import(TracingApplication.class)
public class LoggerAutoConfiguration {
}
