package com.yhy.jakit.starter.crypto.register;

import com.yhy.jakit.starter.crypto.component.enums.Algorithm;
import com.yhy.jakit.starter.crypto.register.factory.asymmetric.ECIESFactory;
import com.yhy.jakit.starter.crypto.register.factory.asymmetric.RSAFactory;
import com.yhy.jakit.starter.crypto.register.factory.asymmetric.SM2Factory;
import com.yhy.jakit.starter.crypto.register.factory.digest.MD5Factory;
import com.yhy.jakit.starter.crypto.register.factory.digest.SHA1Factory;
import com.yhy.jakit.starter.crypto.register.factory.digest.SHA256Factory;
import com.yhy.jakit.starter.crypto.register.factory.digest.SM3Factory;
import com.yhy.jakit.starter.crypto.register.factory.symmetric.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2021-05-31 14:36
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class CryptoRegister {
    private final static Map<Algorithm, CryptoFactory> FACTORY_MAP = new HashMap<>();

    // 注入一些算法实现
    static {
        // 一些对称加密算法
        FACTORY_MAP.put(Algorithm.AES, new AESFactory());
        FACTORY_MAP.put(Algorithm.DES, new DESFactory());
        FACTORY_MAP.put(Algorithm.DESede, new DESedeFactory());
        FACTORY_MAP.put(Algorithm.RC4, new RC4Factory());
        FACTORY_MAP.put(Algorithm.SM4, new SM4Factory());
        FACTORY_MAP.put(Algorithm.Vigenere, new VigenereFactory());

        // 非对称加密算法
        FACTORY_MAP.put(Algorithm.RSA, new RSAFactory());
        FACTORY_MAP.put(Algorithm.SM2, new SM2Factory());
        FACTORY_MAP.put(Algorithm.ECIES, new ECIESFactory());

        // 摘要提取算法
        FACTORY_MAP.put(Algorithm.MD5, new MD5Factory());
        FACTORY_MAP.put(Algorithm.SM3, new SM3Factory());
        FACTORY_MAP.put(Algorithm.SHA1, new SHA1Factory());
        FACTORY_MAP.put(Algorithm.SHA256, new SHA256Factory());
    }

    public static CryptoFactory get(Algorithm algorithm) {
        return FACTORY_MAP.get(algorithm);
    }
}
