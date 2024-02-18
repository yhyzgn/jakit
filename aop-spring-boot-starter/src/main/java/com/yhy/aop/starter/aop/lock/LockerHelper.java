package com.yhy.aop.starter.aop.lock;

import com.yhy.jakit.starter.helper.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * redis 分布式锁实现
 * <p>
 * Created on 2020-07-04 19:41
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@AutoConfigureAfter(RedisHelper.class)
@ConditionalOnBean(RedisHelper.class)
public class LockerHelper {
    @Autowired
    private RedisHelper redisHelper;

    /**
     * 申请一个分布式锁
     *
     * @param key          锁名
     * @param value        锁值
     * @param expiry       过期时间
     * @param expiryUnit   过期时间单位
     * @param retry        重试次数
     * @param interval     重试间隔时间
     * @param intervalUnit 重试间隔时间单位
     * @return 是否申请成功
     * @throws InterruptedException 可能出现的异常
     */
    public boolean tryLock(String key, Object value, long expiry, TimeUnit expiryUnit, int retry, long interval, TimeUnit intervalUnit) throws InterruptedException {
        do {
            Boolean result = redisHelper.template().opsForValue().setIfAbsent(withPrefix(key), value.toString(), expiry, expiryUnit);
            if (null != result && result) {
                return true;
            }
            if (retry > 0) {
                intervalUnit.sleep(interval);
            }
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
        } while (--retry > 0);
        return false;
    }

    /**
     * 解锁
     *
     * @param key 锁名
     * @return 是否成功
     */
    public boolean unlock(String key) {
        Boolean result = redisHelper.template().delete(withPrefix(key));
        return null != result && result;
    }

    /**
     * 给 key 添加前缀
     *
     * @param key 原始 key
     * @return 带前缀的 key
     */
    private String withPrefix(String key) {
        return RedisHelper.withPrefix(redisHelper.keyPrefix(), key, original -> original);
    }
}
