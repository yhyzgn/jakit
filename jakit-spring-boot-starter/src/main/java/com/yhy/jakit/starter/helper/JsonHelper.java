package com.yhy.jakit.starter.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JSON 辅助类
 * <p>
 * Created on 2022-07-26 17:28
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class JsonHelper {
    private final ObjectMapper objectMapper;

    /**
     * 构造方法
     *
     * @param objectMapper objectMapper
     */
    public JsonHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        this.objectMapper.findAndRegisterModules();
    }

    /**
     * 将一个对象转换为 json 字符串
     *
     * @param t   原始对象
     * @param <T> 对象类型
     * @return json 字符串
     */
    public <T> String toJson(T t) {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new JsonException(e.getMessage());
        }
    }

    /**
     * 将 json 字符串转换为基本对象
     *
     * @param json json 字符串
     * @param type 目标对象类
     * @param <T>  目标对象类型
     * @return 目标对象
     */
    public <T> T fromJson(String json, Class<T> type) {
        if (null == json) {
            return null;
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new JsonException(e.getMessage());
        }
    }

    /**
     * 将 json 字符串转换为复杂对象，如 List，Map 等
     *
     * @param json    json 字符串
     * @param typeRef 目标对象类
     * @param <T>     目标对象类型
     * @return 目标对象
     */
    public <T> T fromJson(String json, TypeReference<T> typeRef) {
        if (null == json) {
            return null;
        }
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            throw new JsonException(e.getMessage());
        }
    }

    /**
     * 将 json 字符串转换为基本对象
     *
     * @param json json 字符串
     * @param type 目标对象类
     * @param <T>  目标对象类型
     * @return 目标对象
     */
    public <T> T fromJson(String json, JavaType type) {
        if (null == json) {
            return null;
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new JsonException(e.getMessage());
        }
    }

    /**
     * 将字节数组转换为基本对象
     *
     * @param bytes 字节数组
     * @param type  目标对象类
     * @param <T>   目标对象类型
     * @return 目标对象
     */
    public <T> T fromJson(byte[] bytes, Class<T> type) {
        if (null == bytes) {
            return null;
        }
        try {
            return objectMapper.readValue(bytes, type);
        } catch (IOException e) {
            throw new JsonException(e.getMessage());
        }
    }

    /**
     * 将字节数组转换为复杂对象，如 List，Map 等
     *
     * @param bytes   字节数组
     * @param typeRef 目标对象类
     * @param <T>     目标对象类型
     * @return 目标对象
     */
    public <T> T fromJson(byte[] bytes, TypeReference<T> typeRef) {
        if (null == bytes) {
            return null;
        }
        try {
            return objectMapper.readValue(bytes, typeRef);
        } catch (IOException e) {
            throw new JsonException(e.getMessage());
        }
    }

    /**
     * 将字节数组转换为基本对象
     *
     * @param bytes 字节数组
     * @param type  目标对象类
     * @param <T>   目标对象类型
     * @return 目标对象
     */
    public <T> T fromJson(byte[] bytes, JavaType type) {
        if (null == bytes) {
            return null;
        }
        try {
            return objectMapper.readValue(bytes, type);
        } catch (IOException e) {
            throw new JsonException(e.getMessage());
        }
    }

    /**
     * Json 转换异常
     */
    public static class JsonException extends RuntimeException {

        /**
         * Json 转换异常
         */
        public JsonException() {
            this("Json转换异常");
        }

        /**
         * Json 转换异常
         *
         * @param msg 异常信息
         */
        public JsonException(String msg) {
            super(msg);
        }
    }
}
