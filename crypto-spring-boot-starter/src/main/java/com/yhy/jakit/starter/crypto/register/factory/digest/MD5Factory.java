package com.yhy.jakit.starter.crypto.register.factory.digest;

import cn.hutool.crypto.digest.MD5;
import com.yhy.jakit.starter.crypto.component.enums.Format;
import com.yhy.jakit.starter.crypto.register.CryptoFactory;

/**
 * Created on 2021-05-31 16:59
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class MD5Factory implements CryptoFactory {

    @Override
    public DigesterAlgorithm digester(String salt, Format format) {
        return new AbsDigester<MD5>(salt, format) {
            @Override
            public MD5 algorithm(byte[] salt) {
                return new MD5(salt);
            }
        };
    }
}
