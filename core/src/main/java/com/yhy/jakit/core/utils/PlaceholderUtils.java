package com.yhy.jakit.core.utils;

import com.yhy.jakit.core.descriptor.GetterSetter;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 字符串占位符处理工具类
 * <p>
 * ${参数名} ${obj.field.field}
 * <p>
 * Created on 2021-04-29 15:46
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class PlaceholderUtils {

    /**
     * 处理字符串中的占位符
     * <p>
     * 如：${name} 格式
     *
     * @param text   原始字符串
     * @param names  参数名
     * @param values 参数值
     * @return 处理结果
     */
    public static String resolve(String text, String[] names, Object[] values) {
        Map<String, String> holderMap = placeholderExtract(text);
        if (!CollectionUtils.isEmpty(holderMap)) {
            Map<String, Object> valueMap = placeholderResolve(holderMap, names, values);
            if (!CollectionUtils.isEmpty(valueMap)) {
                for (Map.Entry<String, Object> et : valueMap.entrySet()) {
                    text = text.replace(et.getKey(), null == et.getValue() ? "" : et.getValue().toString());
                }
            }
        }
        return text;
    }

    /**
     * 占位符提取
     *
     * @param src 源
     * @return 提取结果 ${key} = value 格式
     */
    public static Map<String, String> placeholderExtract(String src) {
        Matcher matcher = Pattern.compile("\\$\\{([^{}]+?)}").matcher(src);
        Map<String, String> result = new LinkedHashMap<>();
        while (matcher.find()) {
            result.put(matcher.group(0), matcher.group(1));
        }
        return result;
    }

    /**
     * 处理占位符
     *
     * @param holderMap   占位符表
     * @param paramNames  参数名称列表
     * @param paramValues 参数值列表
     * @return 占位符值
     */
    public static Map<String, Object> placeholderResolve(Map<String, String> holderMap, String[] paramNames, Object[] paramValues) {
        if (null == holderMap || null == paramNames || null == paramValues) {
            return null;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        holderMap.forEach((key, value) -> {
            String[] holderNames = value.split("\\.");
            // 第一级占位符
            int index = ArrayUtils.indexOf(paramNames, parameter -> holderNames[0].equals(parameter));
            if (index >= 0 && index < paramValues.length) {
                // 查询 参数位置
                Object val = paramValues[index];
                if (holderNames.length > 1 && null != val) {
                    List<String> fieldNameList = Stream.of(holderNames).collect(Collectors.toList());
                    // 需移除第一个元素，因为第一个元素只是参数名，已经使用
                    fieldNameList.remove(0);
                    result.put(key, getParamValue(fieldNameList, val));
                } else {
                    result.put(key, val);
                }
            }
        });
        return result;
    }

    /**
     * 按占位符递归获取字段值
     *
     * @param holderNameList 占位符名称表
     * @param obj            字段
     * @return 字段值
     */
    public static Object getParamValue(List<String> holderNameList, Object obj) {
        if (CollectionUtils.isEmpty(holderNameList) || null == obj) {
            return obj;
        }
        try {
            String fieldName = holderNameList.get(0);
            Field field = ReflectionUtils.findField(obj.getClass(), fieldName);
            if (null == field) {
                throw new NoSuchFieldException("No such field " + fieldName + " in class " + obj.getClass());
            }
            obj = GetterSetter.invokeGetter(obj, field);
            // 移除当前占位符字段名
            holderNameList.remove(0);
            // 递归执行
            return getParamValue(holderNameList, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}
