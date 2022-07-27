package com.yhy.jakit.starter.crypto.component.decoder;

import java.util.Base64;

/**
 * Base64 解码器
 * <p>
 * Created on 2021-05-31 9:44
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class Base64Decoder implements Decoder {

    @Override
    public byte[] apply(byte[] src) {
        if (null == src || src.length == 0) {
            return src;
        }
        return Base64.getDecoder().decode(src);
    }
}
