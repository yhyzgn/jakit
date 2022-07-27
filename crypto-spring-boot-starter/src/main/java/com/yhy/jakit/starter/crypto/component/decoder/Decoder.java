package com.yhy.jakit.starter.crypto.component.decoder;

/**
 * 解码器
 * <p>
 * Created on 2021-05-31 9:43
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface Decoder {

    /**
     * 解码
     *
     * @param src 源数据
     * @return 解码后数据
     */
    byte[] apply(byte[] src);
}
