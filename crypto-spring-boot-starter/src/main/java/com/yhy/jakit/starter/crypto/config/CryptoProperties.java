package com.yhy.jakit.starter.crypto.config;

import cn.hutool.crypto.Mode;
import com.yhy.jakit.starter.crypto.component.decoder.Base64Decoder;
import com.yhy.jakit.starter.crypto.component.decoder.Base64URLDecoder;
import com.yhy.jakit.starter.crypto.component.decoder.Decoder;
import com.yhy.jakit.starter.crypto.component.decoder.HexDecoder;
import com.yhy.jakit.starter.crypto.component.encoder.Base64Encoder;
import com.yhy.jakit.starter.crypto.component.encoder.Base64URLEncoder;
import com.yhy.jakit.starter.crypto.component.encoder.Encoder;
import com.yhy.jakit.starter.crypto.component.encoder.HexEncoder;
import com.yhy.jakit.starter.crypto.component.enums.Algorithm;
import com.yhy.jakit.starter.crypto.component.enums.Format;
import com.yhy.jakit.starter.crypto.component.enums.Padding;
import com.yhy.jakit.starter.crypto.exception.UnSupportedCryptAlgorithmException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * Created on 2021-05-31 9:34
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "crypto")
public class CryptoProperties implements InitializingBean, Serializable {

    /**
     * Base package name
     */
    private String basePackage;

    /**
     * Some internal algorithm
     */
    private Algorithm algorithm;

    /**
     * Cipher block mode
     * <p>
     * Default to {@link Mode#ECB}
     */
    private Mode mode;

    /**
     * Internal padding method
     * <p>
     * Default to {@link Padding#PKCS5}
     */
    private Padding padding;

    /**
     * Encoder extends {@link Encoder} and Decoder extends {@link Decoder}
     * <p>
     * Provided internal such as
     * <p>
     * Encoders:
     * <p>
     * {@link Base64Encoder}, {@link Base64URLEncoder}, {@link HexEncoder}
     * <p>
     * Decoders:
     * <p>
     * {@link Base64Decoder}, {@link Base64URLDecoder}, {@link HexDecoder}
     * <p>
     * Default to {@link Base64URLEncoder} and {@link Base64URLDecoder}
     */
    private Format format;

    /**
     * SecretKey for Symmetric, required
     * <p>
     * The optional algorithms are {@link Algorithm#AES}, {@link Algorithm#DES}, {@link Algorithm#DESede}, {@link Algorithm#RC4}, {@link Algorithm#SM4}, {@link Algorithm#Vigenere}
     */
    private String key;

    /**
     * Initialization Vector for Symmetric, must be null if the {@link #mode} is {@link Mode#ECB}, and required with others mode.
     */
    private String iv;

    /**
     * Private key for Asymmetric, required
     */
    private String privateKey;

    /**
     * Public key for Asymmetric, required
     */
    private String publicKey;

    /**
     * Salt value for Digester, not required
     */
    private String salt;

    /**
     * Logging enabled ?
     * <p>
     * Default to true
     */
    private Boolean logging;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(algorithm, "The property 'crypto.algorithm' can not be null.");

        switch (algorithm) {
            case AES:
            case DES:
            case DESede:
            case RC4:
            case SM4:
            case Vigenere:
                // 对称加密
                Assert.hasText(key, "The property 'crypto.key' can not be empty.");
                break;
            case RSA:
            case SM2:
            case ECIES:
                // 非对称加密
                Assert.hasText(privateKey, "The property 'crypto.private-key' can not be empty.");
                Assert.hasText(publicKey, "The property 'crypto.public-key' can not be empty.");
                break;
            case MD5:
            case SM3:
            case SHA1:
            case SHA256:
                // 摘要提取
                break;
            default:
                throw new UnSupportedCryptAlgorithmException(algorithm);
        }

        // 设置一些默认值
        basePackage = null == basePackage ? "" : basePackage;
        mode = null == mode ? Mode.ECB : mode;
        padding = null == padding ? Padding.PKCS5 : padding;
        format = null == format ? Format.BASE64URL : format;
        logging = null == logging || logging;
    }

    public boolean shouldSymmetric() {
        switch (algorithm) {
            case AES:
            case DES:
            case DESede:
            case RC4:
            case SM4:
            case Vigenere:
                return true;
        }
        return false;
    }

    public boolean shouldAsymmetric() {
        switch (algorithm) {
            case RSA:
            case SM2:
            case ECIES:
                return true;
        }
        return false;
    }

    public boolean shouldDigester() {
        switch (algorithm) {
            case MD5:
            case SM3:
            case SHA1:
            case SHA256:
                return true;
        }
        return false;
    }
}
