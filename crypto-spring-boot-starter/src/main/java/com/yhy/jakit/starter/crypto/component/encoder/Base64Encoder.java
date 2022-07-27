package com.yhy.jakit.starter.crypto.component.encoder;

import java.util.Base64;

/**
 * Base64 编码器
 * <p>
 * Created on 2021-05-31 9:42
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class Base64Encoder implements Encoder {

    @Override
    public byte[] apply(byte[] src) {
        if (null == src || src.length == 0) {
            return src;
        }
        return Base64.getEncoder().encode(src);
    }
}
