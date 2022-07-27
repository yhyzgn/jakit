package com.yhy.jakit.starter.crypto.component.encoder;

import java.nio.charset.StandardCharsets;

/**
 * 十六进制编码器
 * <p>
 * Created on 2021-05-31 9:42
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class HexEncoder implements Encoder {

    @Override
    public byte[] apply(byte[] src) {
        if (null == src || src.length == 0) {
            return src;
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                sb.append(0);
            }
            sb.append(hv);
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
