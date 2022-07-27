package com.yhy.jakit.starter.crypto.register.factory.symmetric;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.symmetric.RC4;
import cn.hutool.crypto.symmetric.Vigenere;
import com.yhy.jakit.starter.crypto.component.enums.Format;
import com.yhy.jakit.starter.crypto.component.enums.Padding;
import com.yhy.jakit.starter.crypto.register.CryptoFactory;

/**
 * Created on 2021-05-31 14:51
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class VigenereFactory implements CryptoFactory {

    @Override
    public SymmetricAlgorithm symmetric(String key, String iv, Mode mode, Padding padding, Format format) {
        RC4 rc4 = new RC4(key);

        return new SymmetricAlgorithm() {
            @Override
            public String encrypt(String src) {
                return Vigenere.encrypt(src, key);
            }

            @Override
            public String decrypt(String encrypted) {
                return Vigenere.decrypt(encrypted, key);
            }
        };
    }
}
