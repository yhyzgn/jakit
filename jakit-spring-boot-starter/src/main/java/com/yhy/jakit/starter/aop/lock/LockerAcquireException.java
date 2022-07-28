package com.yhy.jakit.starter.aop.lock;

/**
 * 分布式锁获取异常
 * <p>
 * Created on 2021-03-15 15:45
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class LockerAcquireException extends RuntimeException {

    /**
     * 分布式锁异常
     */
    public LockerAcquireException() {
        this("服务器繁忙，稍候再试");
    }

    /**
     * 分布式锁异常
     *
     * @param message 异常信息
     */
    public LockerAcquireException(String message) {
        super(message);
    }
}
