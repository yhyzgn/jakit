package com.yhy.jakit.starter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.yhy.jakit.util.IPUtils;
import com.yhy.jakit.util.deserializer.LocalDateTimeDeserializer;
import com.yhy.jakit.util.id.SnowFlake;
import com.yhy.jakit.util.serializer.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 自动装配
 * <p>
 * Created on 2022-07-26 15:18
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ComponentScan
public class JakitAutoConfiguration {
    private static final Random RD = new Random();

    /**
     * 创建雪花算法 bean
     *
     * @return 雪花算法实例
     */
    @Bean
    public SnowFlake snowFlake() {
        long workId = Math.abs(IPUtils.localIP().replaceAll("\\.", "").hashCode() % 32);
        long dataCenterId = Math.abs(RD.nextLong() % 32);
        return SnowFlake.create(workId, dataCenterId);
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.findAndRegisterModules();
        return om;
    }

    /**
     * 时间类型统一响应为 ms 时间戳
     *
     * @return json 转换器
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer());
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer());
        };
    }
}
