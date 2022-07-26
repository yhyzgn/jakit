package com.yhy.jakit.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机数工具
 * <p>
 * Created on 2022-07-26 14:31
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class RandUtils {
    private final static ThreadLocalRandom TLR = ThreadLocalRandom.current();
    private final static List<String> POOL = new ArrayList<>();

    static {
        int begin = 65;
        for (int i = begin; i < begin + 26; i++) {
            POOL.add(((char) i) + "");
        }
        begin = 97;
        for (int i = begin; i < begin + 26; i++) {
            POOL.add(((char) i) + "");
        }
        for (int i = 0; i < 9; i++) {
            POOL.add(i + "");
        }
    }

    /**
     * 获取指定长度的随机字符串
     * <p>
     * [a-zA-Z0-9]
     *
     * @param length 指定长度
     * @return 结果
     */
    public static String getString(int length) {
        StringBuilder sb = new StringBuilder();
        int size = POOL.size();
        for (int i = 0; i < length; i++) {
            sb.append(POOL.get(TLR.nextInt(size)));
        }
        return sb.toString();
    }

    public static String getCode(int length) {
        // 默认4位
        int code =
            length == 5 ? TLR.nextInt(89999) + 10000 :
                length == 6 ? TLR.nextInt(899999) + 100000 :
                    TLR.nextInt(8999) + 1000;
        return String.valueOf(code);
    }
}
