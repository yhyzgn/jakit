package com.yhy.jakit.util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * LocalDateTime 序列化
 * <p>
 * Created on 2022-07-26 14:19
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (null != value) {
            ZoneId zone = ZoneId.systemDefault();
            ZonedDateTime zdt = value.atZone(zone);
            generator.writeNumber(Date.from(zdt.toInstant()).getTime());
        }
    }
}