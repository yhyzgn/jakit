package com.yhy.jakit.starter.crypto.component.decoder;

import java.nio.charset.StandardCharsets;

/**
 * 十六进制解码器
 * <p>
 * Created on 2021-05-31 9:44
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class HexDecoder implements Decoder {

    @Override
    public byte[] apply(byte[] src) {
        String hexStr = new String(src, StandardCharsets.UTF_8).toUpperCase();
        int length = hexStr.length() / 2;
        char[] hexChars = hexStr.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
