package com.yhy.jakit.simple.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created on 2022-07-29 10:12
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.yhy")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
