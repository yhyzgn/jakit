package com.yhy.jakit.starter.crypto.register;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.asymmetric.AbstractAsymmetricCrypto;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.yhy.jakit.starter.crypto.component.enums.Algorithm;
import com.yhy.jakit.starter.crypto.component.enums.Format;
import com.yhy.jakit.starter.crypto.component.enums.Padding;
import org.springframework.util.StringUtils;

/**
 * 加密解密工厂模式
 * <p>
 * Created on 2021-05-31 14:39
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public interface CryptoFactory {

    /**
     * 对称算法
     *
     * @param key     秘钥
     * @param iv      偏移向量
     * @param mode    对称模式 {@link Mode}
     * @param padding 填充模式 {@link Padding}
     * @param format  格式化，编码解码方式 {@link Format}
     * @return 算法对象 {@link SymmetricAlgorithm}
     */
    default SymmetricAlgorithm symmetric(String key, String iv, Mode mode, Padding padding, Format format) {
        return null;
    }

    /**
     * 非对称算法
     *
     * @param privateKey 私钥
     * @param publicKey  公钥
     * @param format     格式化，编码解码方式 {@link Format}
     * @return 算法对象 {@link AsymmetricAlgorithm}
     */
    default AsymmetricAlgorithm asymmetric(String privateKey, String publicKey, Format format) {
        return null;
    }

    /**
     * 摘要提取算法
     *
     * @param salt   盐
     * @param format 格式化，编码解码方式 {@link Format}
     * @return 算法对象 {@link DigesterAlgorithm}
     */
    default DigesterAlgorithm digester(String salt, Format format) {
        return null;
    }

    /**
     * 对称算法
     * <p>
     * 用 <br/>
     * {@link Algorithm#AES} <br/>
     * {@link Algorithm#DES} <br/>
     * {@link Algorithm#DESede} <br/>
     * {@link Algorithm#RC4} <br/>
     * {@link Algorithm#SM4} <br/>
     * {@link Algorithm#Vigenere} <br/>
     * 中的对称算法和 {@link Mode} 加密，{@link Padding} 方式填充，{@link Format} 方式编码和解码
     */
    interface SymmetricAlgorithm {

        /**
         * 加密
         *
         * @param src 原数据，明文
         * @return 加密并编码后的数据，密文
         */
        String encrypt(String src);

        /**
         * 解密
         *
         * @param encrypted 待解密数据，密文
         * @return 解码并解密后的数据，明文
         */
        String decrypt(String encrypted);
    }

    /**
     * 对称算法抽象实现
     * <p>
     * 实现 {@link SymmetricAlgorithm}
     *
     * @param <T> 具体的算法类
     */
    abstract class AbsSymmetricAlgorithm<T extends SymmetricCrypto> implements SymmetricAlgorithm {
        protected final Format format;
        protected final T crypto;

        public AbsSymmetricAlgorithm(String key, String iv, Mode mode, Padding padding, Format format) {
            this.format = format;

            crypto = algorithm(mode, padding, StrUtil.utf8Bytes(key), StringUtils.hasText(iv) ? StrUtil.utf8Bytes(iv) : null);
        }

        /**
         * 确定具体的算法
         *
         * @param mode    对称模式 {@link Mode}
         * @param padding 填充模式 {@link Padding}
         * @param key     秘钥
         * @param iv      偏移向量
         * @return 算法
         */
        public abstract T algorithm(Mode mode, Padding padding, byte[] key, byte[] iv);

        @Override
        public String encrypt(String src) {
            byte[] encrypted = crypto.encrypt(src);
            encrypted = format.encoder().apply(encrypted);
            return StrUtil.utf8Str(encrypted);
        }

        @Override
        public String decrypt(String encrypted) {
            byte[] bys = format.decoder().apply(StrUtil.utf8Bytes(encrypted));
            byte[] decrypted = crypto.decrypt(bys);
            return StrUtil.utf8Str(decrypted);
        }
    }

    /**
     * 非对称算法
     * <p>
     * 用 <br/>
     * {@link Algorithm#RSA} <br/>
     * {@link Algorithm#SM2} <br/>
     * {@link Algorithm#ECIES} <br/>
     * 中的非对称算法加密，{@link Format} 方式编码和解码
     */
    interface AsymmetricAlgorithm {

        /**
         * 加密
         *
         * @param src 原数据，明文
         * @return 加密并编码后的数据，密文
         */
        String encrypt(String src);

        /**
         * 解密
         *
         * @param encrypted 待解密数据，密文
         * @return 解码并解密后的数据，明文
         */
        String decrypt(String encrypted);
    }

    /**
     * 非对称算法抽象实现
     * <p>
     * 实现 {@link AsymmetricAlgorithm}
     *
     * @param <T> 具体的算法类
     */
    abstract class AbsAsymmetricAlgorithm<T extends AbstractAsymmetricCrypto<?>> implements AsymmetricAlgorithm {
        private final Format format;
        private final T crypto;

        public AbsAsymmetricAlgorithm(String privateKey, String publicKey, Format format) {
            this.format = format;

            byte[] kPri = format.decoder().apply(StrUtil.utf8Bytes(privateKey));
            byte[] kPub = format.decoder().apply(StrUtil.utf8Bytes(publicKey));

            this.crypto = algorithm(kPri, kPub);
        }

        /**
         * 确定具体的算法
         *
         * @param privateKey 私钥
         * @param publicKey  公钥
         * @return 算法
         */
        public abstract T algorithm(byte[] privateKey, byte[] publicKey);

        @Override
        public String encrypt(String src) {
            byte[] encrypted = crypto.encrypt(src, KeyType.PublicKey);
            encrypted = format.encoder().apply(encrypted);
            return StrUtil.utf8Str(encrypted);
        }

        @Override
        public String decrypt(String encrypted) {
            byte[] bys = format.decoder().apply(StrUtil.utf8Bytes(encrypted));
            byte[] decrypted = crypto.decrypt(bys, KeyType.PrivateKey);
            return StrUtil.utf8Str(decrypted);
        }
    }

    /**
     * 摘要提取算法
     * <p>
     * 用 <br/>
     * {@link Algorithm#MD5} <br/>
     * {@link Algorithm#SM3} <br/>
     * {@link Algorithm#SHA1} <br/>
     * {@link Algorithm#SHA256} <br/>
     * 中的摘要提取算法加密，{@link Format} 方式编码和解码
     */
    interface DigesterAlgorithm {

        /**
         * 加密
         *
         * @param src 原数据，明文
         * @return 加密并编码后的数据，密文
         */
        String digest(String src);

        /**
         * 匹配
         *
         * @param target 待判断数据，明文
         * @param hashed 已加密数据，密文
         * @return 是否匹配
         */
        boolean matched(String target, String hashed);
    }

    abstract class AbsDigester<T extends Digester> implements DigesterAlgorithm {
        protected final Format format;
        protected final T digester;

        public AbsDigester(String salt, Format format) {
            this.format = format;

            digester = algorithm(StringUtils.hasText(salt) ? StrUtil.utf8Bytes(salt) : null);
        }

        /**
         * 确定具体的算法
         *
         * @param salt 盐
         * @return 算法
         */
        public abstract T algorithm(byte[] salt);

        @Override
        public String digest(String src) {
            byte[] digested = digester.digest(src);
            digested = format.encoder().apply(digested);
            return StrUtil.utf8Str(digested);
        }

        @Override
        public boolean matched(String target, String digested) {
            return digested.equals(digest(target));
        }
    }
}
