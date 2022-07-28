package com.yhy.jakit.starter.logging.writer;

import com.yhy.jakit.starter.logging.model.Log;

/**
 * 日志统一输出接口
 * <p>
 * Created on 2020-11-03 13:19
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public interface LoggingWriter {

    /**
     * 输出日志
     *
     * @param lg 日志信息
     * @throws Exception 可能发生的异常
     */
    void write(Log lg) throws Exception;
}
