package com.yhy.jakit.starter.crypto.register.factory.digest;

import cn.hutool.crypto.digest.SM3;
import com.yhy.jakit.starter.crypto.component.enums.Format;
import com.yhy.jakit.starter.crypto.register.CryptoFactory;

/**
 * Created on 2021-05-31 16:59
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class SM3Factory implements CryptoFactory {

    @Override
    public DigesterAlgorithm digester(String salt, Format format) {
        return new AbsDigester<SM3>(salt, format) {
            @Override
            public SM3 algorithm(byte[] salt) {
                return new SM3(salt);
            }
        };
    }
}
