package com.yhy.jakit.core.inter;

/**
 * Created on 2022-03-23 19:46
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleTestInterface implements TestInterface {

    @Override
    public String test() {
        return "SimpleTestInterface";
    }
}
