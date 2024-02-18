package com.yhy.aop.starter.aop.limit;

import com.yhy.jakit.starter.helper.RedisHelper;
import com.yhy.jakit.util.internal.Maps;
import com.yhy.jakit.util.system.SystemClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 辅助类
 * <p>
 * Created on 2021-04-29 14:59
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@AutoConfigureAfter(RedisHelper.class)
@ConditionalOnBean(RedisHelper.class)
public class LimiterHelper {
    @Autowired
    private RedisHelper redisHelper;

    /**
     * 申请一个分布式限流器令牌
     *
     * @param key      令牌桶标识
     * @param quota    单位时间内生成令牌的数量
     * @param period   规定一定数量令牌的单位时间，同时也是生成一批令牌的单位时间（s）
     * @param quantity 每次需要的令牌数，默认为 1
     * @param capacity 令牌桶容量
     * @return 令牌是否申请成功
     */
    public boolean acquire(String key, long quota, Duration period, long quantity, long capacity) {
        try {
            return withLuaScript(key, quota, period, quantity, capacity);
        } catch (Exception e) {
            return withTemplate(key, quota, period, quantity, capacity);
        }
    }

    @SuppressWarnings("rawtypes")
    private boolean withLuaScript(String key, long quota, Duration period, long quantity, long capacity) {
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("script/rate_limiter_bucket.lua"));
        script.setResultType(List.class);
        // 使用 StringRedisTemplate，需将所有参数转换成 String
        List res = redisHelper.script(script, Collections.singletonList(withPrefix(key)), capacity + "", period.getSeconds() + "", quota + "", quantity + "");
        return !CollectionUtils.isEmpty(res) && ((Long) res.get(0)) > 0;
    }

    private synchronized boolean withTemplate(String key, long quota, Duration period, long quantity, double capacity) {
        // 1、先判断令牌桶是否存在
        // - 不存在：初始化令牌桶，并设置过期时间
        // -   存在：计算从上一次更新到现在这段时间内应该要生成的令牌数，更新令牌数量，并设置过期时间
        // 2、查询目前令牌数
        // - 满足所需令牌数：减掉所需令牌数后更新令牌数量，并设置过期时间
        // -        不满足：已超过限流频率

        long now = SystemClock.now() / 1000L;
        key = withPrefix(key);

        StringRedisTemplate template = redisHelper.template();
        HashOperations<String, Object, Object> ops = template.opsForHash();

        // 1、判断令牌桶是否存在
        if (!redisHelper.exists(key)) {
            // 不存在，初始化
            // 保存并设置过期时间
            ops.putAll(key, Maps.of(
                "tokens", String.valueOf(capacity),
                "timestamp", String.valueOf(now)
            ));
            template.expire(key, period);
        } else {
            // 存在
            // 计算从上一次更新到现在这段时间内应该要生成的令牌数
            double tokens = Double.parseDouble(Objects.requireNonNull(ops.get(key, "tokens")).toString());
            long last = Long.parseLong(Objects.requireNonNull(ops.get(key, "timestamp")).toString());
            double supply = (((double) now - (double) last) / (double) period.getSeconds()) * (double) quota;
            if (supply > 0) {
                // 重置令牌数量
                tokens = Math.min(tokens + supply, capacity);
                ops.putAll(key, Maps.of(
                    "tokens", String.valueOf(tokens),
                    "timestamp", String.valueOf(now)
                ));
                template.expire(key, period);
            }
        }

        double tokens = Double.parseDouble(Objects.requireNonNull(ops.get(key, "tokens")).toString());

        if (tokens < quantity) {
            // 令牌数量不足，返回0表示已超过限流，同时返回剩余令牌数
            return false;
        }
        // 令牌充足
        // 重置剩余令牌数
        tokens -= quantity;
        ops.putAll(key, Maps.of(
            "tokens", String.valueOf(tokens),
            "timestamp", String.valueOf(now)
        ));
        template.expire(key, period);
        // 令牌获取成功
        return true;
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
