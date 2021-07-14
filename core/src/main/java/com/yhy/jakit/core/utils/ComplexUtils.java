package com.yhy.jakit.core.utils;

import java.util.Map;
import java.util.TreeMap;

/**
 * 字符串复杂度检查工具
 * <p>
 * Created on 2021-07-13 10:19
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class ComplexUtils {
    /**
     * 检查字符串复杂度
     * <p>
     * 字符串必须包含字母（大小写不敏感）、数字、特殊字符至少两种
     * <p>
     * {@link Complexity#KINDS_2_INSENSITIVE}
     *
     * @param src 字符串
     * @throws IllegalArgumentException 字符串复杂度异常
     */
    public static void check(String src) throws IllegalArgumentException {
        check(src, Complexity.KINDS_2_INSENSITIVE);
    }

    /**
     * 检查字符串复杂度
     *
     * @param password   字符串
     * @param complexity 复杂度枚举
     * @throws IllegalArgumentException 字符串复杂度异常
     */
    public static void check(String password, Complexity complexity) throws IllegalArgumentException {
        check(password, complexity.pattern);
    }

    /**
     * 检查字符串复杂度
     *
     * @param password 字符串
     * @param pattern  复杂度正则表达式
     * @throws IllegalArgumentException 字符串复杂度异常
     */
    public static void check(String password, String pattern) throws IllegalArgumentException {
        if (!password.matches(pattern)) {
            throw new IllegalArgumentException("字符串必须包含字母、数字、特殊字符至少两种");
        }
    }

    /**
     * 字符串复杂度枚举
     * <p>
     * <a href="https://blog.csdn.net/qq_36961226/article/details/110429934">参考地址</a>
     */
    public enum Complexity {
        /**
         * 字符串必须包含字母（大小写敏感）、数字、特殊字符至少两种
         * <p>
         * 排除：小写字母 | 大写字母 | 数字 | 特殊字符 这几种情况，剩下的至少包含了两种字符
         */
        KINDS_2_SENSITIVE("^(?![a-z]+$)(?![A-Z]+$)(?![0-9]+$)(?![\\W_]+$)[a-zA-Z0-9\\W_]{6,20}$"),

        /**
         * 字符串必须包含字母（大小写不敏感）、数字、特殊字符至少两种
         * <p>
         * 排除：小写字母-大写字母 | 数字 | 特殊字符 这几种情况，剩下的至少包含了两种字符
         */
        KINDS_2_INSENSITIVE("^(?![a-zA-Z]+$)(?![0-9]+$)(?![\\W_]+$)[a-zA-Z0-9\\W_]{6,20}$"),

        /**
         * 字符串必须包含字母（大小写敏感）、数字、特殊字符至少三种
         * <p>
         * 排除：小写字母-大写字母 | 小写字母-数字 | 小写字母-特殊字符 | 大写字母-数字 | 大写字母-特殊字符 | 数字-特殊字符 这几种情况，剩下的至少包含了三种字符
         */
        KINDS_3_SENSITIVE("^(?![a-zA-Z]+$)(?![a-z0-9]+$)(?![a-z\\W_]+$)(?![A-Z0-9]+$)(?![A-Z\\W_]+$)(?![0-9\\W_]+$)[a-zA-Z0-9\\W_]{6,20}$"),

        /**
         * 字符串必须包含字母（大小写不敏感）、数字、特殊字符至少三种
         * <p>
         * 排除：小写字母-大写字母-数字 | 小写字母-大写字母-特殊字符 | 数字-特殊字符 这几种情况，剩下的至少包含了三种字符
         */
        KINDS_3_INSENSITIVE("^(?![a-zA-Z0-9]+$)(?![a-zA-Z\\W_]+$)(?![0-9\\W_]+$)[a-zA-Z0-9\\W_]{6,20}$"),

        /**
         * 字符串必须包含字母（大小写敏感）、数字、特殊字符至少四种
         * <p>
         * 排除：小写字母-大写字母-数字 | 小写字母-大写字母-特殊字符 | 小写字母-数字-特殊字符 | 大写字母-数字-特殊字符 这几种情况，剩下的至少包含了四种字符
         * <p>
         * 至少包含四种字符时，将不存在 大小写不敏感 的情况，否则无法满足四种类型
         */
        KIND_4_SENSITIVE("^(?![a-zA-Z0-9]+$)(?![a-zA-Z\\W_]+$)(?![a-z0-9\\W_]+$)(?![A-Z0-9\\W_]+$)[a-zA-Z0-9\\W_]{6,20}$"),
        ;

        /**
         * 复杂度正则表达式
         */
        private final String pattern;

        /**
         * 内置构造器
         *
         * @param pattern 复杂度正则表达式
         */
        Complexity(String pattern) {
            this.pattern = pattern;
        }

        /**
         * 获取复杂度正则表达式
         *
         * @return 复杂度正则表达式
         */
        public String getPattern() {
            return pattern;
        }
    }

    public static void main(String[] args) {
        Map<String, String> testMap = new TreeMap<>((o1, o2) -> 1);

        testMap.put("ABCDEFGHIG", "全部大写");
        testMap.put("abcdefghig", "全部小写");
        testMap.put("0123456789", "全部数字");
        testMap.put("!@#$%^&*()", "全部特殊字符");
        testMap.put("ABCDEabcde", "大写和小写");
        testMap.put("ABCDE01234", "大写和数字");
        testMap.put("ABCDE!@#$%", "大写和特殊字符");
        testMap.put("abcde01234", "小写和数字");
        testMap.put("abcde!@#$%", "小写字母和特殊字符");
        testMap.put("01234!@#$%", "数字和特殊字符");
        testMap.put("Aa4!", "长度不够6位数");
        testMap.put("ABCDE01234!@#$%", "大写字母&数字&特殊字符");
        testMap.put("ABCDEabcde!@#$%", "大写字母&小写字母&特殊字符");
        testMap.put("ABCDEabcde01234", "大写字母&小写字母&数字");
        testMap.put("abcde01234!@#_$%", "小写字母&数字&特殊字符");
        testMap.put("ABCabc012@-#", "全部的四种");

        test(testMap, Complexity.KINDS_2_SENSITIVE);
        test(testMap, Complexity.KINDS_2_INSENSITIVE);
        test(testMap, Complexity.KINDS_3_SENSITIVE);
        test(testMap, Complexity.KINDS_3_INSENSITIVE);
        test(testMap, Complexity.KIND_4_SENSITIVE);
    }

    private static void test(Map<String, String> testMap, Complexity complexity) {
        System.out.println("====================================================== " + complexity + " ======================================================");
        testMap.forEach((key, value) -> {
            System.out.println(value + " " + key.matches(complexity.getPattern()));
        });
    }
}
