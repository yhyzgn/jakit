package com.yhy.jakit.starter.crypto.register.factory.symmetric;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.symmetric.RC4;
import com.yhy.jakit.starter.crypto.component.enums.Format;
import com.yhy.jakit.starter.crypto.component.enums.Padding;
import com.yhy.jakit.starter.crypto.register.CryptoFactory;

import java.nio.charset.StandardCharsets;

/**
 * Created on 2021-05-31 14:51
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class RC4Factory implements CryptoFactory {

    @Override
    public SymmetricAlgorithm symmetric(String key, String iv, Mode mode, Padding padding, Format format) {
        RC4 rc4 = new RC4(key);

        return new SymmetricAlgorithm() {
            @Override
            public String encrypt(String src) {
                byte[] encrypted = rc4.encrypt(src);
                encrypted = format.encoder().apply(encrypted);
                return new String(encrypted, StandardCharsets.UTF_8);
            }

            @Override
            public String decrypt(String encrypted) {
                byte[] bys = format.decoder().apply(encrypted.getBytes(StandardCharsets.UTF_8));
                return rc4.decrypt(bys);
            }
        };
    }
}
