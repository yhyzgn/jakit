package com.yhy.jakit.starter.crypto.register.factory.asymmetric;

import cn.hutool.crypto.asymmetric.ECIES;
import com.yhy.jakit.starter.crypto.component.enums.Format;
import com.yhy.jakit.starter.crypto.register.CryptoFactory;

/**
 * Created on 2021-05-31 16:40
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class ECIESFactory implements CryptoFactory {

    @Override
    public AsymmetricAlgorithm asymmetric(String privateKey, String publicKey, Format format) {
        return new AbsAsymmetricAlgorithm<ECIES>(privateKey, publicKey, format) {

            @Override
            public ECIES algorithm(byte[] privateKey, byte[] publicKey) {
                return new ECIES(privateKey, publicKey);
            }
        };
    }
}
