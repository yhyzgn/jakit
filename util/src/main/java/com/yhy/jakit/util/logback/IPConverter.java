package com.yhy.jakit.util.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.yhy.jakit.util.IPUtils;
import com.yhy.jakit.util.StringUtils;

/**
 * Logback IP 获取工具
 * <p>
 * Created on 2022-07-26 14:25
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class IPConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        String ip = IPUtils.localIP();
        return StringUtils.hasText(ip) ? ip : "0.0.0.0";
    }
}