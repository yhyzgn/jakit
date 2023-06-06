package com.yhy.jakit.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
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
        List<Class<?>> classList = ClassUtils.implemented("com.yhy", TestInterface.class);
        System.out.println(classList);
    }

    @Test
    public void testLocalDateBetween() {
        LocalDate now = LocalDate.now();
        LocalDateTimeUtils.daysBetween(now.minusDays(1), now).forEach(System.out::println);
    }
}
