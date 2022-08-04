package com.yhy.jakit.simple.crypto;

import com.yhy.jakit.starter.crypto.EnableCrypto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created on 2021-05-31 9:13
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@EnableCrypto
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
