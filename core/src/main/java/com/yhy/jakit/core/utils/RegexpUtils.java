package com.yhy.jakit.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 正则工具类
 * <p>
 * Created on 2021-04-05 19:20
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class RegexpUtils {
    // 数字
    private final static String REG_NUMBER = "^\\d+$";
    // 手机号码
    private final static String REG_MOBILE = "^1[3,4,5,6,7,8,9]\\d{9}$";
    // 邮箱
    private final static String REG_EMAIL = "^(\\w+\\-?\\w+)(\\.\\w+\\-?\\w+)*@(\\w{2,8}\\-?\\w+\\.){1,3}\\w{2,8}$";
    // 网址
    private final static String REG_URL = "^https?://(\\w+\\-?\\w+)+(\\.\\w+\\-?\\w+)+(/\\w+)*/?$";
    // 身份证号(15位或者18位，最后一位可以为字母)
    //
    // 假设18位身份证号码:41000119910101123X 410001 19910101 123X
    // ^开头
    // [1-9] 第一位1-9中的一个 4
    // \\d{5} 五位数字 10001（前六位省市县地区）
    // (18|19|20) 19（现阶段可能取值范围18xx-20xx年）
    // \\d{2} 91（年份）
    // ((0[1-9])|(10|11|12)) 01（月份）
    // (([0-2][1-9])|10|20|30|31)01（日期）
    // \\d{3} 三位数字 123（第十七位奇数代表男，偶数代表女）
    // [0-9Xx] 0123456789Xx其中的一个 X（第十八位为校验值）
    // $结尾
    //
    // 假设15位身份证号码:410001910101123 410001 910101 123
    // ^开头
    // [1-9] 第一位1-9中的一个 4
    // \\d{5} 五位数字 10001（前六位省市县地区）
    // \\d{2} 91（年份）
    // ((0[1-9])|(10|11|12)) 01（月份）
    // (([0-2][1-9])|10|20|30|31)01（日期）
    // \\d{3} 三位数字 123（第十五位奇数代表男，偶数代表女），15位身份证不含X
    // $结尾
    private final static String REG_ID_CARD = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" + "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";

    private RegexpUtils() {
        throw new UnsupportedOperationException("RegexpUtils can not be instantiate.");
    }

    /**
     * 检查是否是数字
     *
     * @param text 待检查文本
     * @return 是否是数字
     */
    public static boolean isNumber(String text) {
        return match(text, REG_NUMBER);
    }

    /**
     * 检查是否是手机号码
     *
     * @param text 待检查文本
     * @return 是否是手机号码
     */
    public static boolean isMobile(String text) {
        return match(text, REG_MOBILE);
    }

    /**
     * 检查是否是邮箱地址
     *
     * @param text 待检查文本
     * @return 是否是邮箱地址
     */
    public static boolean isEmail(String text) {
        return match(text, REG_EMAIL);
    }

    /**
     * 检查是否是url
     *
     * @param text 待检查文本
     * @return 是否是url
     */
    public static boolean isUrl(String text) {
        return match(text, REG_URL);
    }

    /**
     * 检查字符串是否匹配
     *
     * @param text 待检查字符串
     * @param reg  匹配正则表达式
     * @return 是否匹配
     */
    public static boolean match(String text, String reg) {
        return null != text && Pattern.compile(reg).matcher(text).matches();
    }

    /**
     * 隐藏手机号中间四位
     *
     * @param mobile 手机号码
     * @return 结果
     */
    public static String hideMobile(String mobile) {
        if (isMobile(mobile)) {
            return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        return mobile;
    }

    /**
     * 是否是身份证号
     *
     * @param text 省份证号
     * @return 是否
     */
    public static boolean isIdCard(String text) {
        boolean matches = text.matches(REG_ID_CARD);
        // 判断第18位校验值
        if (matches) {
            if (text.length() == 18) {
                char[] charArray = text.toCharArray();
                // 前十七位加权因子
                int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                // 这是除以11后，可能产生的11位余数对应的验证码
                String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                int sum = 0;
                for (int i = 0; i < idCardWi.length; i++) {
                    int current = Integer.parseInt(String.valueOf(charArray[i]));
                    int count = current * idCardWi[i];
                    sum += count;
                }
                char idCardLast = charArray[17];
                int idCardMod = sum % 11;

                // 计算校验码是否正确
                return idCardY[idCardMod].equalsIgnoreCase(String.valueOf(idCardLast));
            }
        }
        return false;
    }

    /**
     * 是否是营业执照
     *
     * @param text 编号
     * @return 是否
     */
    public static boolean isCreditCode(String text) {
        if (text.length() == 18) {
            String baseCode = "0123456789ABCDEFGHJKLMNPQRTUWXY";
            char[] baseCodeArray = baseCode.toCharArray();
            Map<Character, Integer> codes = new HashMap<Character, Integer>();
            for (int i = 0; i < baseCode.length(); i++) {
                codes.put(baseCodeArray[i], i);
            }
            char[] businessCodeArray = text.toCharArray();
            char check = businessCodeArray[17];
            if (baseCode.indexOf(check) == -1) {
                return false;
            }
            int[] wi = {1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};
            int sum = 0;
            for (int i = 0; i < 17; i++) {
                char key = businessCodeArray[i];
                if (baseCode.indexOf(key) == -1) {
                    return false;
                }
                sum += (codes.get(key) * wi[i]);
            }
            int temp = sum % 31;
            //统一社会信用代码校验规则有说余数为0时，校验位为0
            if (temp == 0) {
                temp = 31;
            }
            int value = 31 - temp;
            return value == codes.get(check);
        }
        return false;
    }

    /**
     * 银行卡校验算法
     *
     * @param text 银行卡号
     * @return 是否是银行卡
     */
    private static boolean isBankcard(String text) {
        if (text.length() < 15 || text.length() > 19) {
            return false;
        }
        char bit = getBankCardCheckCode(text.substring(0, text.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return text.charAt(text.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     *
     * @param text 银行卡号
     * @return 校验位
     */
    private static char getBankCardCheckCode(String text) {
        if (text == null || text.trim().length() == 0 || !text.matches("\\d+")) {
            // 如果传的不是数据返回N
            return 'N';
        }
        char[] chs = text.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }
}
