package com.yhy.jakit.starter.crypto.exec;

import com.yhy.jakit.starter.crypto.config.CryptoProperties;
import com.yhy.jakit.starter.crypto.exception.UnSupportedCryptAlgorithmException;
import com.yhy.jakit.starter.crypto.register.CryptoFactory;
import com.yhy.jakit.starter.crypto.register.CryptoRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 执行器
 * <p>
 * 手动执行加解密操作时可直接获取该 bean 执行对应方法
 * <p>
 * Created on 2021-03-30 18:03
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class CryptoExecutor {

    @Autowired
    private CryptoProperties properties;

    /**
     * 加密
     *
     * @param src 原始数据，明文
     * @return 加密结果，密文
     * @throws UnSupportedCryptAlgorithmException 未支持的加密算法
     */
    public String encrypt(String src) throws UnSupportedCryptAlgorithmException {
        if (!StringUtils.hasLength(src)) {
            return src;
        }
        if (properties.shouldSymmetric()) {
            return symmetric().encrypt(src);
        } else if (properties.shouldAsymmetric()) {
            return asymmetric().encrypt(src);
        } else if (properties.shouldDigester()) {
            return digester().digest(src);
        } else {
            throw new UnSupportedCryptAlgorithmException(properties.getAlgorithm());
        }
    }

    /**
     * 解密
     *
     * @param encrypted 待解密数据，密文
     * @return 解密结果，明文
     * @throws UnSupportedCryptAlgorithmException 未支持的解密算法
     */
    public String decrypt(String encrypted) throws UnSupportedCryptAlgorithmException {
        if (!StringUtils.hasLength(encrypted)) {
            return encrypted;
        }
        if (properties.shouldSymmetric()) {
            return symmetric().decrypt(encrypted);
        } else if (properties.shouldAsymmetric()) {
            return asymmetric().decrypt(encrypted);
        } else if (properties.shouldDigester()) {
            // 摘要签名时，encrypted 参数只能传明文，此处就当做明文使用
            return digester().digest(encrypted);
        } else {
            throw new UnSupportedCryptAlgorithmException(properties.getAlgorithm());
        }
    }

    private CryptoFactory.SymmetricAlgorithm symmetric() {
        CryptoFactory factory = CryptoRegister.get(properties.getAlgorithm());
        return factory.symmetric(properties.getKey(), properties.getIv(), properties.getMode(), properties.getPadding(), properties.getFormat());
    }

    private CryptoFactory.AsymmetricAlgorithm asymmetric() {
        CryptoFactory factory = CryptoRegister.get(properties.getAlgorithm());
        return factory.asymmetric(properties.getPrivateKey(), properties.getPublicKey(), properties.getFormat());
    }

    private CryptoFactory.DigesterAlgorithm digester() {
        CryptoFactory factory = CryptoRegister.get(properties.getAlgorithm());
        return factory.digester(properties.getSalt(), properties.getFormat());
    }
}
