package com.yhy.jakit.util;

import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 反射工具类
 * <p>
 * Created on 2021-04-05 18:38
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class ReflectionUtils {

    private final static Map<Class<?>, Field[]> FIELD_CACHE = new HashMap<>();

    private ReflectionUtils() {
        throw new UnsupportedOperationException("ReflectionUtils can not be instantiate manual.");
    }

    /**
     * 该类是否被某个注解注解
     *
     * @param clazz      类
     * @param annotation 注解
     * @return 是否被注解
     */
    public static boolean isAnnotated(Class<?> clazz, Class<? extends Annotation> annotation) {
        return null != clazz && null != annotation && null != clazz.getAnnotation(annotation);
    }

    /**
     * 获取某个类当前声明的字段
     * <p>
     * 不包含父类字段
     *
     * @param clazz 类
     * @return 字段
     */
    public static Field[] getDeclaredFields(Class<?> clazz) {
        if (null != clazz) {
            if (FIELD_CACHE.containsKey(clazz)) {
                return FIELD_CACHE.get(clazz);
            }
            Field[] fields = clazz.getDeclaredFields();
            FIELD_CACHE.put(clazz, fields.length > 0 ? fields : new Field[0]);
            return fields;
        }
        return null;
    }

    /**
     * 遍历处理某个类中的全部字段
     * <p>
     * 包括父类字段
     *
     * @param clazz    类
     * @param callback 处理回调
     */
    public static void doFields(Class<?> clazz, FieldCallback callback) {
        doFields(clazz, callback, null);
    }

    /**
     * 遍历处理某个类中的全部字段
     * <p>
     * 包括父类字段
     *
     * @param clazz    类
     * @param callback 处理回调
     * @param filter   匹配条件
     */
    public static void doFields(Class<?> clazz, FieldCallback callback, FieldFilter filter) {
        Class<?> targetClass = clazz;
        do {
            Field[] fields = getDeclaredFields(targetClass);
            for (Field field : fields) {
                if (null != filter && !filter.matches(field)) {
                    continue;
                }
                if (null != callback) {
                    try {
                        callback.apply(field);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + e);
                    }
                }
            }
            targetClass = clazz.getSuperclass();
        } while (null != targetClass && targetClass != Object.class);
    }

    /**
     * 获取一个字段
     * <p>
     * 按字段名获取
     *
     * @param clazz 类
     * @param name  字段名
     * @return 字段
     */
    @Nullable
    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, null);
    }

    /**
     * 获取一个字段
     * <p>
     * 按字段名和字段类型获取
     *
     * @param clazz 类
     * @param name  字段名
     * @param type  字段类型
     * @return 字段
     */
    @Nullable
    public static Field findField(Class<?> clazz, @Nullable String name, @Nullable Class<?> type) {
        if (null == clazz) {
            throw new IllegalArgumentException("Class can not be null");
        }
        if (null == name && null == type) {
            throw new IllegalArgumentException("Either name or type of the field must be specified");
        }
        Class<?> searchType = clazz;
        while (Object.class != searchType && searchType != null) {
            Field[] fields = getDeclaredFields(searchType);
            for (Field field : fields) {
                if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType()))) {
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * 处理回调
     */
    @FunctionalInterface
    public interface FieldCallback {

        /**
         * 处理字段回调方法
         *
         * @param field 字段
         * @throws IllegalArgumentException 参数异常
         * @throws IllegalAccessException   字段访问异常
         */
        void apply(Field field) throws IllegalArgumentException, IllegalAccessException;
    }

    /**
     * 字段过滤器
     */
    @FunctionalInterface
    public interface FieldFilter {

        /**
         * 过滤满足条件的字段
         *
         * @param field 字段
         * @return 是否满足条件
         */
        boolean matches(Field field);
    }
}
