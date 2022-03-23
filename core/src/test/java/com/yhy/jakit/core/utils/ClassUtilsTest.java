package com.yhy.jakit.core.utils;

import com.yhy.jakit.core.inter.TestInterface;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Created on 2022-03-23 19:45
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class ClassUtilsTest {

    @Test
    public void testClass() {
        List<Class<?>> classList = ClassUtils.getClassList("com.yhy", TestInterface.class);
        System.out.println(classList);
    }
}
