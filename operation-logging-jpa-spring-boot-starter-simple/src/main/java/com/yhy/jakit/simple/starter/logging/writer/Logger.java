package com.yhy.jakit.simple.starter.logging.writer;

import com.yhy.jakit.starter.logging.model.Log;
import com.yhy.jakit.starter.logging.writer.LoggingWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created on 2021-12-10 10:42
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
public class Logger implements LoggingWriter {

    @Override
    public void write(Log lg) throws Exception {
        log.info("simple writer: {}", lg);
    }
}
