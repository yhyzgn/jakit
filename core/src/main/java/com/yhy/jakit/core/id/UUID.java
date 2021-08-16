package com.yhy.jakit.core.id;

/**
 * UUID 工具
 * <p>
 * Created on 2021-08-16 15:13
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class UUID {

    /**
     * 获取默认类型的 UUID 值
     *
     * @return 默认类型的 UUID 值
     */
    public static String get() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * 获取无 - 连接的 UUID 值
     *
     * @return 无 - 连接的 UUID 值
     */
    public static String getNoMinus() {
        return get().replace("-", "");
    }

    /**
     * 获取默认类型的 UUID 大写值
     *
     * @return 默认类型的 UUID 大写值
     */
    public static String upper() {
        return get().toUpperCase();
    }

    /**
     * 获取默认类型的 UUID 小写值
     *
     * @return 默认类型的 UUID 小写值
     */
    public static String lower() {
        return get().toLowerCase();
    }

    /**
     * 获取无 - 连接的 UUID 大写值
     *
     * @return 无 - 连接的 UUID 大写值
     */
    public static String upperNoMinus() {
        return getNoMinus().toUpperCase();
    }

    /**
     * 获取无 - 连接的 UUID 小写值
     *
     * @return 无 - 连接的 UUID 小写值
     */
    public static String lowerNoMinus() {
        return getNoMinus().toLowerCase();
    }
}
