package com.yhy.jakit.util.descriptor;

import com.yhy.jakit.util.descriptor.consumer.*;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 解决 java 9.0+ 通过 PropertyDescriptor 获取 getter 和 setter 方法报错的问题
 * <p>
 * Created on 2020-05-02 21:18
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 * <p>
 * 参考资料：
 * https://dzone.com/articles/setters-method-handles-and-java-11
 * https://dzone.com/articles/hacking-lambda-expressions-in-java
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class GetterSetter {

    /**
     * 获取 getter 方法
     *
     * @param lookup 方法查询器
     * @param getter 需要获取的getter方法
     * @return getter 方法执行器
     * @throws Exception 可能出现的异常
     */
    public static Function getGetter(final MethodHandles.Lookup lookup, final MethodHandle getter) throws Exception {
        final CallSite site = LambdaMetafactory.metafactory(
                lookup,
                "apply",
                MethodType.methodType(Function.class),
                MethodType.methodType(Object.class, Object.class),
                getter,
                getter.type());
        try {
            return (Function) site.getTarget().invokeExact();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    /**
     * 获取 setter 方法
     *
     * @param lookup    方法查询器
     * @param setter    需要获取的setter方法
     * @param valueType setter方法的参数类型
     * @return setter 方法执行器
     * @throws Exception 可能出现的异常
     */
    public static BiConsumer getSetter(final MethodHandles.Lookup lookup, final MethodHandle setter, final Class<?> valueType) throws Exception {
        try {
            // 8 种基本原始类型需要单独处理，否则 Java 9.0.4 和 Java 11 无法正常运行
            if (valueType.isPrimitive()) {
                if (valueType == Double.class || valueType == double.class) {
                    OBJDoubleConsumer consumer = (OBJDoubleConsumer) getSetterCallSite(lookup, setter, OBJDoubleConsumer.class, double.class).getTarget().invokeExact();
                    return (a, b) -> consumer.accept(a, (double) b);
                } else if (valueType == Integer.class || valueType == int.class) {
                    OBJIntegerConsumer consumer = (OBJIntegerConsumer) getSetterCallSite(lookup, setter, OBJIntegerConsumer.class, int.class).getTarget().invokeExact();
                    return (a, b) -> consumer.accept(a, (int) b);
                } else if (valueType == Long.class || valueType == long.class) {
                    OBJLongConsumer consumer = (OBJLongConsumer) getSetterCallSite(lookup, setter, OBJLongConsumer.class, long.class).getTarget().invokeExact();
                    return (a, b) -> consumer.accept(a, (long) b);
                } else if (valueType == Short.class || valueType == short.class) {
                    OBJShortConsumer consumer = (OBJShortConsumer) getSetterCallSite(lookup, setter, OBJShortConsumer.class, short.class).getTarget().invokeExact();
                    return (a, b) -> consumer.accept(a, (short) b);
                } else if (valueType == Character.class || valueType == char.class) {
                    OBJCharConsumer consumer = (OBJCharConsumer) getSetterCallSite(lookup, setter, OBJCharConsumer.class, char.class).getTarget().invokeExact();
                    return (a, b) -> consumer.accept(a, (char) b);
                } else if (valueType == Boolean.class || valueType == boolean.class) {
                    OBJBooleanConsumer consumer = (OBJBooleanConsumer) getSetterCallSite(lookup, setter, OBJBooleanConsumer.class, boolean.class).getTarget().invokeExact();
                    return (a, b) -> consumer.accept(a, (boolean) b);
                } else if (valueType == Byte.class || valueType == byte.class) {
                    OBJByteConsumer consumer = (OBJByteConsumer) getSetterCallSite(lookup, setter, OBJByteConsumer.class, byte.class).getTarget().invokeExact();
                    return (a, b) -> consumer.accept(a, (byte) b);
                } else if (valueType == Float.class || valueType == float.class) {
                    OBJFloatConsumer consumer = (OBJFloatConsumer) getSetterCallSite(lookup, setter, OBJFloatConsumer.class, float.class).getTarget().invokeExact();
                    return (a, b) -> consumer.accept(a, (float) b);
                } else {
                    throw new RuntimeException("Type is not supported yet: " + valueType.getName());
                }
            } else {
                return (BiConsumer) getSetterCallSite(lookup, setter, BiConsumer.class, Object.class).getTarget().invokeExact();
            }
        } catch (Exception e) {
            throw e;
        } catch (Throwable th) {
            throw new Error(th);
        }
    }

    /**
     * 获取字段属性集
     *
     * @param clazz     所属类
     * @param fieldName 字段名
     * @return 属性集
     * @throws Exception 可能出现的异常
     */
    public static PropertyDescriptor descriptor(Class<?> clazz, String fieldName) throws Exception {
        final BeanInfo info = Introspector.getBeanInfo(clazz);
        final Function<String, PropertyDescriptor> property = name -> Stream.of(info.getPropertyDescriptors())
                .filter(p -> name.equals(p.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Not found: " + name));
        return property.apply(fieldName);
    }

    /**
     * 获取 setter 方法
     *
     * @param lookup        方法查询器
     * @param setter        需要获取的setter方法
     * @param interfaceType 执行器接口类型
     * @param valueType     setter方法的参数类型
     * @return setter 方法执行器
     * @throws LambdaConversionException 可能出现的异常
     */
    private static CallSite getSetterCallSite(MethodHandles.Lookup lookup, MethodHandle setter, Class<?> interfaceType, Class<?> valueType) throws LambdaConversionException {
        return LambdaMetafactory.metafactory(lookup,
                "accept",
                MethodType.methodType(interfaceType),
                MethodType.methodType(void.class, Object.class, valueType),
                setter,
                setter.type());
    }

    /**
     * 获取 getter 方法
     *
     * @param clazz 所属类
     * @param field 字段
     * @return getter 方法
     * @throws Exception 可能出现的异常
     */
    public static Function getGetter(Class<?> clazz, Field field) throws Exception {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        return getGetter(lookup, lookup.unreflect(descriptor(clazz, field.getName()).getReadMethod()));
    }

    /**
     * 获取 setter 方法
     *
     * @param clazz 所属类
     * @param field 字段
     * @return setter 方法
     * @throws Exception 可能出现的异常
     */
    public static BiConsumer getSetter(Class<?> clazz, Field field) throws Exception {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        PropertyDescriptor pd = descriptor(clazz, field.getName());
        return getSetter(lookup, lookup.unreflect(pd.getWriteMethod()), pd.getPropertyType());
    }

    /**
     * 执行 getter 方法
     *
     * @param obj   需执行 getter 方法的对象
     * @param field 字段
     * @return getter 方法
     * @throws Exception 可能出现的异常
     */
    public static Object invokeGetter(Object obj, Field field) throws Exception {
        Function func = getGetter(obj.getClass(), field);
        return func.apply(obj);
    }

    /**
     * 执行 setter 方法
     *
     * @param obj   需执行 setter 方法的对象
     * @param field 字段
     * @param value 字段值
     * @throws Exception 可能出现的异常
     */
    public static void invokeSetter(Object obj, Field field, Object value) throws Exception {
        BiConsumer consumer = getSetter(obj.getClass(), field);
        consumer.accept(obj, value);
    }
}