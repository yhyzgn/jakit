package com.yhy.jakit.starter.crypto.register.factory.symmetric;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.symmetric.SM4;
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
public class SM4Factory implements CryptoFactory {

    @Override
    public SymmetricAlgorithm symmetric(String key, String iv, Mode mode, Padding padding, Format format) {
        return new AbsSymmetricAlgorithm<SM4>(key, iv, mode, padding, format) {
            @Override
            public SM4 algorithm(Mode mode, Padding padding, byte[] key, byte[] iv) {
                return new SM4(mode.name(), padding.getValue(), key, iv);
            }
        };
    }
}
