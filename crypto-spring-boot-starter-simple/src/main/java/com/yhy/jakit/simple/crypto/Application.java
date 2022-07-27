package com.yhy.jakit.simple.crypto;

import com.yhy.jakit.starter.crypto.component.enums.Format;
import com.yhy.jakit.starter.crypto.register.CryptoFactory;
import com.yhy.jakit.starter.crypto.register.factory.asymmetric.SM2Factory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created on 2021-05-31 9:13
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {

//        String prv = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQg72p39akvBQBXxxdOVSTG60B4TXyWOtB5jG21GAyjP96gCgYIKoEcz1UBgi2hRANCAAS6VvT4N0phSms0bSR6H5f9EGGQ2TujziCiEmitYo2oTydI1gu97mSnsokFroziPmBCgOUvZX5nVUUU1UoJyLXz";
//        String pub = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEulb0-DdKYUprNG0keh-X_RBhkNk7o84gohJorWKNqE8nSNYLve5kp7KJBa6M4j5gQoDlL2V-Z1VFFNVKCci18w==";

        String prv = "308193020100301306072a8648ce3d020106082a811ccf5501822d047930770201010420ff54e3914a41191006633b5f08fcc5e42dbdf2c26c18d55dd9501f0d889c151ea00a06082a811ccf5501822da1440342000410ee64f73e0acb2380bada2697e80a22b39968ac72833b7692968ff2c21bc88e8afcbdc1b89c3382089415cea907e61b02060194675a16c4d48c5e3a57c49388";
        String pub = "3059301306072a8648ce3d020106082a811ccf5501822d0342000410ee64f73e0acb2380bada2697e80a22b39968ac72833b7692968ff2c21bc88e8afcbdc1b89c3382089415cea907e61b02060194675a16c4d48c5e3a57c49388";

//        CryptoFactory.AsymmetricAlgorithm asy = new SM2Factory().asymmetric(prv, pub, new Base64Encoder(), new Base64Decoder());
        CryptoFactory.AsymmetricAlgorithm asy = new SM2Factory().asymmetric(prv, pub, Format.HEX);
        System.out.println(asy.encrypt("你好啊"));

//        System.out.println(asy.decrypt("BFP8fx8V_cBG6XNAEg284WULomCg2W5r8Usmw70QIGSP7Wxyo8Cu8LmrefTzutM_79kNoJ6aPnI83W_lVTA7EHyYktKGb74u2ymE9inGdWoiEe57gG8hq6-0TnpT9z7k1uFMBNX14Jup6HNuMw=="));
//        System.out.println(asy.decrypt("04c44c25d1cf629a6c88fb92405f10980b1f8c3436eee5aacd322647acd9f13812447a5368bc13e42e9d10867acedbd7c66fe83b13b18f5858a1c520c493551d78aed3f8d9d250c597e42a0a9ab83c07026f624d5cb3bc451390088273de74221885810ec7cbde"));
        System.out.println(asy.decrypt("044fd2407ae9b39d7e8080c15fa39ad392ce0ba26829345c6422c9fa1a8746ad4c5f42c3a1d6aaab0a8f16405afdcb3fb708c2a200b48ebd9b12adcaf686e4a060c6182903dd43e9b401afccc8504d9360d572a14d372dc7665fc2eb762f2e34c45cf50283c7c07b6e4d"));

        SpringApplication.run(Application.class, args);
    }
}
