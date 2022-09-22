package com.yhy.jakit.starter.crypto;

import com.yhy.jakit.starter.crypto.aop.doer.AspectDoer;
import com.yhy.jakit.starter.crypto.aop.doer.DefaultAspectDoer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 2021-05-31 9:20
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ComponentScan
public class CryptoAutoConfiguration {

    /**
     * 创建默认的 bean
     * <br/>
     * <code>@ConditionalOnMissingBean</code> 推荐使用 <code>@Bean</code> 方式条件注入
     *
     * @return AspectDoer bean
     */
    @Bean
    @ConditionalOnMissingBean
    public AspectDoer aspectDoer() {
        return new DefaultAspectDoer();
    }
}
