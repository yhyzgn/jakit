package com.yhy.jakit.starter.crypto.register.factory.asymmetric;

import cn.hutool.crypto.asymmetric.RSA;
import com.yhy.jakit.starter.crypto.component.enums.Format;
import com.yhy.jakit.starter.crypto.register.CryptoFactory;

/**
 * Created on 2021-05-31 16:14
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class RSAFactory implements CryptoFactory {

    @Override
    public AsymmetricAlgorithm asymmetric(String privateKey, String publicKey, Format format) {
        return new AbsAsymmetricAlgorithm<RSA>(privateKey, publicKey, format) {

            @Override
            public RSA algorithm(byte[] privateKey, byte[] publicKey) {
                return new RSA(privateKey, publicKey);
            }
        };
    }
}
