package com.yhy.jakit.starter.crypto.register.factory.digest;

import cn.hutool.crypto.digest.Digester;
import com.yhy.jakit.starter.crypto.component.enums.Format;
import com.yhy.jakit.starter.crypto.register.CryptoFactory;

/**
 * Created on 2021-05-31 16:59
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class SHA256Factory implements CryptoFactory {

    @Override
    public DigesterAlgorithm digester(String salt, Format format) {
        return new AbsDigester<SHA256>(salt, format) {
            @Override
            public SHA256 algorithm(byte[] salt) {
                return new SHA256(salt);
            }
        };
    }

    public static class SHA256 extends Digester {
        private static final long serialVersionUID = 1L;

        /**
         * 构造
         *
         * @param salt 盐值
         */
        public SHA256(byte[] salt) {
            this(salt, 0, 1);
        }

        /**
         * 构造
         *
         * @param salt        盐值
         * @param digestCount 摘要次数，当此值小于等于1,默认为1。
         */
        public SHA256(byte[] salt, int digestCount) {
            this(salt, 0, digestCount);
        }

        /**
         * 构造
         *
         * @param salt         盐值
         * @param saltPosition 加盐位置，即将盐值字符串放置在数据的index数，默认0
         * @param digestCount  摘要次数，当此值小于等于1,默认为1。
         */
        public SHA256(byte[] salt, int saltPosition, int digestCount) {
            super(cn.hutool.crypto.digest.DigestAlgorithm.SHA256);
            this.salt = salt;
            this.saltPosition = saltPosition;
            this.digestCount = digestCount;
        }
    }
}
