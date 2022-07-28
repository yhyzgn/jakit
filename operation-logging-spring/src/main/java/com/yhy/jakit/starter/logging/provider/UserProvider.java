package com.yhy.jakit.starter.logging.provider;

/**
 * 日志信息提供者
 * <p>
 * Created on 2021-07-08 10:38
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UserProvider {

    /**
     * 用户唯一标识
     *
     * @return 用户唯一标识
     */
    String identity();
}
