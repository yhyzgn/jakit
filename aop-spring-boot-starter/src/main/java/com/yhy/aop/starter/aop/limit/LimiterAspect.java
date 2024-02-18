package com.yhy.aop.starter.aop.limit;

import com.yhy.jakit.util.PlaceholderUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * 分布式限流器切面
 * <p>
 * Created on 2021-04-29 14:55
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
@ConditionalOnBean(LimiterHelper.class)
public class LimiterAspect {
    @Autowired(required = false)
    private LimiterHelper helper;

    /**
     * 环绕增强
     *
     * @param point 切点
     * @return 方法执行结果
     * @throws Throwable 可能出现的异常
     */
    @Around("@annotation(com.yhy.aop.starter.aop.limit.Limiter)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        if (null != helper) {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            Limiter limiter = method.getAnnotation(Limiter.class);

            // 从方法参数中填充 key 占位符
            // 支持占位符从参数自动填充
            String key = PlaceholderUtils.resolve(limiter.key(), signature.getParameterNames(), point.getArgs());

            long capacity = limiter.capacity();
            if (capacity <= 0L) {
                // 默认值
                capacity = limiter.quota();
            }

            log.info("分布式限流器【{}】申请令牌中...", key);
            if (!helper.acquire(key, limiter.quota(), Duration.of(limiter.period(), limiter.periodUnit()), limiter.quantity(), capacity)) {
                log.error("分布式限流器【{}】申请令牌失败", key);
                throw new RateLimiterException(limiter.message());
            }
            log.info("分布式限流器【{}】申请令牌成功", key);
        }
        return null == point.getArgs() || point.getArgs().length == 0 ? point.proceed() : point.proceed(point.getArgs());
    }
}
