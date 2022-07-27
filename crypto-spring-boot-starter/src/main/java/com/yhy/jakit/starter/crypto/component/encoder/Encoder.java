package com.yhy.jakit.starter.crypto.component.encoder;

/**
 * 编码器
 * <p>
 * Created on 2021-05-31 9:41
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface Encoder {

    /**
     * 编码
     *
     * @param src 源数据
     * @return 编码后数据
     */
    byte[] apply(byte[] src);
}
