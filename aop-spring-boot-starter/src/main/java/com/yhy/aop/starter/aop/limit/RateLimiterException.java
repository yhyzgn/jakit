package com.yhy.aop.starter.aop.limit;

/**
 * 限流异常
 * <p>
 * Created on 2021-01-29 15:32
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class RateLimiterException extends RuntimeException {

    /**
     * 限流异常
     */
    public RateLimiterException() {
        this("请求过于频繁");
    }

    /**
     * 限流异常
     *
     * @param message 异常信息
     */
    public RateLimiterException(String message) {
        super(message);
    }
}
