package com.yhy.jakit.starter.internal;

import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Spring Assert 包装类
 * <p>
 * Created on 2023-07-03 11:39
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Assert extends org.springframework.util.Assert {

    /**
     * 条件为真
     *
     * @param expression 条件表达式
     * @param t          异常对象
     * @param <T>        异常类型
     */
    public static <T extends RuntimeException> void isTrue(boolean expression, T t) {
        if (!expression) {
            throw t;
        }
    }

    /**
     * 对象为空
     *
     * @param object 对象
     * @param t      异常对象
     * @param <T>    异常类型
     */
    public static <T extends RuntimeException> void isNull(@Nullable Object object, T t) {
        if (null != object) {
            throw t;
        }
    }

    /**
     * 对象不为空
     *
     * @param object 对象
     * @param t      异常对象
     * @param <T>    异常类型
     */
    public static <T extends RuntimeException> void notNull(@Nullable Object object, T t) {
        if (null == object) {
            throw t;
        }
    }

    /**
     * 文本有长度
     *
     * @param text 文本
     * @param t    异常对象
     * @param <T>  异常类型
     */
    public static <T extends RuntimeException> void hasLength(@Nullable String text, T t) {
        if (!StringUtils.hasLength(text)) {
            throw t;
        }
    }

    /**
     * 文本有数据
     *
     * @param text 文本
     * @param t    异常对象
     * @param <T>  异常类型
     */
    public static <T extends RuntimeException> void hasText(@Nullable String text, T t) {
        if (!StringUtils.hasText(text)) {
            throw t;
        }
    }

    /**
     * 文本不包含
     *
     * @param textToSearch 全文
     * @param substring    子串
     * @param t            异常对象
     * @param <T>          异常类型
     */
    public static <T extends RuntimeException> void doesNotContain(@Nullable String textToSearch, String substring, T t) {
        if (StringUtils.hasLength(textToSearch) && StringUtils.hasLength(substring) && textToSearch.contains(substring)) {
            throw t;
        }
    }

    /**
     * 对象数组不为空
     *
     * @param array 对象数组
     * @param t     异常对象
     * @param <T>   异常类型
     */
    public static <T extends RuntimeException> void notEmpty(@Nullable Object[] array, T t) {
        if (ObjectUtils.isEmpty(array)) {
            throw t;
        }
    }

    /**
     * 对象数组不包含空节点
     *
     * @param array 对象数组
     * @param t     异常对象
     * @param <T>   异常类型
     */
    public static <T extends RuntimeException> void noNullElements(@Nullable Object[] array, T t) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    throw t;
                }
            }
        }
    }

    /**
     * 集合不为空
     *
     * @param collection 集合
     * @param t          异常对象
     * @param <T>        异常类型
     */
    public static <T extends RuntimeException> void notEmpty(@Nullable Collection<?> collection, T t) {
        if (CollectionUtils.isEmpty(collection)) {
            throw t;
        }
    }

    /**
     * 集合不包含空节点
     *
     * @param collection 集合
     * @param t          异常对象
     * @param <T>        异常类型
     */
    public static <T extends RuntimeException> void noNullElements(@Nullable Collection<?> collection, T t) {
        if (collection != null) {
            for (Object element : collection) {
                if (element == null) {
                    throw t;
                }
            }
        }
    }

    /**
     * Map 集合不为空
     *
     * @param map 集合
     * @param t   异常对象
     * @param <T> 异常类型
     */
    public static <T extends RuntimeException> void notEmpty(@Nullable Map<?, ?> map, T t) {
        if (CollectionUtils.isEmpty(map)) {
            throw t;
        }
    }

    /**
     * 实例所属类判断
     *
     * @param type 类
     * @param obj  实例
     * @param t    异常对象
     * @param <T>  异常类型
     */
    public static <T extends RuntimeException> void isInstanceOf(Class<?> type, @Nullable Object obj, T t) {
        notNull(type, t);
        if (!type.isInstance(obj)) {
            throw t;
        }
    }

    /**
     * 类继承关系判断
     *
     * @param superType 父类
     * @param subType   子类
     * @param t         异常对象
     * @param <T>       异常类型
     */
    public static <T extends RuntimeException> void isAssignable(Class<?> superType, @Nullable Class<?> subType, T t) {
        notNull(superType, t);
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw t;
        }
    }

    /**
     * 主动抛出一个异常
     *
     * @param t   异常对象
     * @param <T> 异常类型
     */
    public static <T extends RuntimeException> void thr(T t) {
        throw t;
    }
}
