package com.yhy.aop.starter.aop.lock;

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
import java.util.UUID;

/**
 * 分布式锁切面
 * <p>
 * Created on 2020-07-04 20:15
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
@ConditionalOnBean(LockerHelper.class)
public class LockerAspect {
    @Autowired(required = false)
    private LockerHelper helper;

    /**
     * 环绕增强
     *
     * @param point 切点
     * @return 方法执行结果
     * @throws Throwable 可能出现的异常
     */
    @Around("@annotation(com.yhy.aop.starter.aop.lock.Locker)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        if (null == helper) {
            return null == point.getArgs() || point.getArgs().length == 0 ? point.proceed() : point.proceed(point.getArgs());
        }
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Locker locker = method.getAnnotation(Locker.class);

        // 从方法参数中填充 key 占位符
        // 支持占位符从参数自动填充
        String key = PlaceholderUtils.resolve(locker.key(), signature.getParameterNames(), point.getArgs());

        log.info("申请分布式锁【{}】...", key);
        boolean locked = helper.tryLock(key, UUID.randomUUID().toString(), locker.expiry(), locker.expiryUnit(), locker.retry(), locker.interval(), locker.intervalUnit());
        if (!locked) {
            log.error("获取锁【{}】失败", key);
            throw new LockerAcquireException(locker.message());
        }
        log.info("获取锁【{}】成功！", key);
        try {
            return null == point.getArgs() || point.getArgs().length == 0 ? point.proceed() : point.proceed(point.getArgs());
        } finally {
            // 方法执行完后是否自动解锁
            if (locker.autoUnlock()) {
                log.info("操作执行完成后自动解锁【{}】结果：【{}】。", key, helper.unlock(key));
            }
        }
    }
}

