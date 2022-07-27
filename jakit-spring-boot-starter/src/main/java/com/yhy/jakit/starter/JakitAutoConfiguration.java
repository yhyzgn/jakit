package com.yhy.jakit.starter;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.yhy.jakit.util.IPUtils;
import com.yhy.jakit.util.deserializer.LocalDateTimeDeserializer;
import com.yhy.jakit.util.id.SnowFlake;
import com.yhy.jakit.util.serializer.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

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
public class JakitAutoConfiguration implements InitializingBean {
    private static final Random RD = new Random();

    @Autowired
    private Executor executor;


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

    @Bean
    @ConditionalOnMissingBean
    public Executor executor() {
        // 根据 ThreadPoolTaskExecutor 创建建线程池
        ThreadPoolTaskExecutor tempExecutor = new ThreadPoolTaskExecutor();
        // 为线程设置初始的线程数量 5 条线程
        tempExecutor.setCorePoolSize(5);
        // 为线程设置最大的线程数量 10 条线程
        tempExecutor.setMaxPoolSize(10);
        // 为任务队列设置最大 200 任务数量
        tempExecutor.setQueueCapacity(200);
        // 设置 超出初始化线程的存在时间为 60 秒
        // 也就是 如果现有线程数超过 5 则会对超出的空闲线程设置摧毁时间也就是 60 秒
        tempExecutor.setKeepAliveSeconds(60);
        // 设置线程前缀
        tempExecutor.setThreadNamePrefix("task-executor-");
        // 线程池的饱和策略 我这里设置的是 CallerRunsPolicy 也就是由用调用者所在的线程来执行任务 共有四种
        // AbortPolicy：直接抛出异常，这是默认策略；
        // CallerRunsPolicy：用调用者所在的线程来执行任务；
        // DiscardOldestPolicy：丢弃阻塞队列中靠最前的任务，并执行当前任务；
        // DiscardPolicy：直接丢弃任务；
        tempExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 设置在关闭线程池时是否等待任务完成
        tempExecutor.setWaitForTasksToCompleteOnShutdown(true);
        // 设置等待终止的秒数
        tempExecutor.setAwaitTerminationSeconds(60);
        // 初始化
        tempExecutor.initialize();
        // ttl wrapped
        return TtlExecutors.getTtlExecutor(tempExecutor);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // check and wrapping
        if (TtlExecutors.isTtlWrapper(executor)) {
            executor = TtlExecutors.getTtlExecutor(executor);
        }
    }
}
