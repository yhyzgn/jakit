package com.yhy.jakit.starter.dynamic.datasource.redis.exception;

/**
 * Created on 2021-05-25 14:59
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class RedisUrlSyntaxException extends RuntimeException {

    private final String url;

    public RedisUrlSyntaxException(String url, Exception cause) {
        super(buildMessage(url), cause);
        this.url = url;
    }

    public RedisUrlSyntaxException(String url) {
        super(buildMessage(url));
        this.url = url;
    }

    String getUrl() {
        return this.url;
    }

    private static String buildMessage(String url) {
        return "Invalid Redis URL '" + url + "'";
    }

}
