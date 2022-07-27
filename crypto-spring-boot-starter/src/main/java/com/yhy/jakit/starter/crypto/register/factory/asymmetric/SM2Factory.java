package com.yhy.jakit.starter.crypto.register.factory.asymmetric;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;
import com.yhy.jakit.starter.crypto.component.enums.Format;
import com.yhy.jakit.starter.crypto.register.CryptoFactory;

/**
 * Created on 2021-05-31 16:14
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class SM2Factory implements CryptoFactory {

    @Override
    public AsymmetricAlgorithm asymmetric(String privateKey, String publicKey, Format format) {
        return new AbsAsymmetricAlgorithm<SM2>(privateKey, publicKey, format) {

            @Override
            public SM2 algorithm(byte[] privateKey, byte[] publicKey) {
                return SmUtil.sm2(privateKey, publicKey);
            }
        };
    }
}
