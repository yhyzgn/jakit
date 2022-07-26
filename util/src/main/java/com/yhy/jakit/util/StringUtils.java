package com.yhy.jakit.util;

import org.jetbrains.annotations.Nullable;

/**
 * String 工具类
 * <p>
 * Created on 2022-07-26 14:26
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public interface StringUtils {

    static boolean hasText(@Nullable CharSequence str) {
        return str != null && str.length() > 0 && containsText(str);
    }

    static boolean hasText(@Nullable String str) {
        return str != null && !str.isEmpty() && containsText(str);
    }

    static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
