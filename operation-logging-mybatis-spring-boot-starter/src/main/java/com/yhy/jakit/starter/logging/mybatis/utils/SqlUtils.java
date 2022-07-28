package com.yhy.jakit.starter.logging.mybatis.utils;


import com.yhy.jakit.starter.logging.model.Type;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.burningwave.core.assembler.StaticComponentContainer;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.*;

/**
 * Created on 2022-06-14 17:30
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public abstract class SqlUtils {

    static {
        StaticComponentContainer.Modules.exportAllToAll();
    }

    /**
     * 解析BoundSql，生成不含占位符的SQL语句
     *
     * @param configuration
     * @param boundSql
     * @return java.lang.String
     */
    public static String showSql(Configuration configuration, BoundSql boundSql) {
        if (null == boundSql) {
            return "";
        }
        Object parameterObject = boundSql.getParameterObject();
        // 获取方法参数
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        // 格式化SQL
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            // 类型解析器
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    String[] s = metaObject.getObjectWrapper().getGetterNames();
                    Arrays.toString(s);
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        return sql;
    }

    /**
     * 若为字符串或者日期类型，则在参数两边添加''
     *
     * @param obj
     * @return java.lang.String
     */
    private static String getParameterValue(Object obj) {
        String value;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
        }
        return value;
    }


    /**
     * 操作前获取数据(修改或者删除的时候需要查询操作前的数据)
     *
     * @param stat
     * @param boundSql
     * @param parameter
     * @param jdbcTemplate
     * @return
     */
    public static List<List<Map<String, Object>>> getDataBeforeOperate(MappedStatement stat, BoundSql boundSql, Object parameter, JdbcTemplate jdbcTemplate) {
        List<List<Map<String, Object>>> resultList = new ArrayList<>();
        // SELECT 和 INSERT 操作不用记录修改前的值，直接返回
        if (parameter == null || SqlCommandType.SELECT.equals(stat.getSqlCommandType()) || SqlCommandType.INSERT.equals(stat.getSqlCommandType())) {
            return resultList;
        }

        boolean batchFlag = getBatchFlag(parameter);
        String tableName = getTableName(boundSql, stat.getSqlCommandType());
        String primaryKeyName = getFieldName(boundSql, stat.getSqlCommandType(), batchFlag);

        List<Map<String, Object>> primaryKeyList = reflectGetListValue(primaryKeyName, parameter, batchFlag);
        // 遍历主键列表
        for (Map<String, Object> map : primaryKeyList) {
            for (String key : map.keySet()) {
                // 获取主键值
                Object primaryKeyValue = map.get(key);
                queryData(jdbcTemplate, tableName, primaryKeyName, primaryKeyValue, resultList);
            }
        }
        return resultList;
    }

    public static Type opType(MappedStatement stat) {
        switch (stat.getSqlCommandType()) {
            case INSERT:
                return Type.Insert;
            case UPDATE:
                return Type.Update;
            case DELETE:
                return Type.Delete;
            case SELECT:
                return Type.Select;
            default:
                return Type.Unknown;
        }
    }

    /**
     * 获取批次标记
     *
     * @param parameter
     * @return boolean
     */
    private static boolean getBatchFlag(Object parameter) {
        return null != parameter && !parameter.toString().contains("param");
    }

    /**
     * 获取表名
     * 截取规则 ：
     * 1.替换sql命令类型标签（select delete insert update）
     * 2.替换掉from标签
     * 3.截取从0开始，\n结束
     */
    private static String getTableName(BoundSql boundSql, SqlCommandType cmdType) {
        String sql = boundSql.getSql();
        switch (cmdType) {
            case INSERT:
                // INSERT INTO table_name (xxx) VALUES (xxxx);
                int intoIndex = sql.contains("into") ? sql.indexOf("into") : sql.contains("INTO") ? sql.indexOf("INTO") : 0;
                return sql.substring(intoIndex + 5, sql.indexOf("(")).trim();
            case DELETE:
            case SELECT:
                // delete from table_name where xxxx
                int fromIndex = sql.contains("from") ? sql.indexOf("from") : sql.contains("FROM") ? sql.indexOf("FROM") : 0;
                sql = sql.substring(fromIndex + 5).trim();
                return sql.substring(0, sql.indexOf(" "));
            case UPDATE:
                // update table_name set xxx
                sql = sql.replaceAll("update", "").replaceAll("UPDATE", "").trim();
                return sql.substring(0, sql.indexOf(" "));
            default:
                return "";
        }
    }

    /**
     * 获取sql获取FieldName
     * 截取规则 ：
     * （1）去掉\n ,？，=
     * （2）从where索引位置开始截取, 到sqlStr最后一个位置 ,替换掉where操作符，去除空格
     * (3) batchFlag=true代表批量，需要从新解析（截取fieldName,从beginIndex=0开始截取，endIndex=;结束
     */
    private static String getFieldName(BoundSql boundSql, SqlCommandType cmdType, boolean batchFlag) {
        if (cmdType == SqlCommandType.UNKNOWN || !StringUtils.hasText(boundSql.getSql())) {
            return "";
        }
        String sqlStr = boundSql.getSql().replaceAll("\n", "").replaceAll("\\?", "").replaceAll("=", "");
        int start = 0;
        if (sqlStr.contains("where")) {
            start = sqlStr.indexOf("where");
        } else if (sqlStr.contains("WHERE")) {
            start = sqlStr.indexOf("WHERE");
        }
        String fieldName = sqlStr.substring(start).replace("where", "").replace("WHERE", "").trim();
        //解析批量
        if (batchFlag) {
            fieldName = (fieldName.contains(";") ? fieldName.substring(0, fieldName.indexOf(";")) : fieldName).trim();
        }
        return fieldName;
    }

    /**
     * 根据属性名获取属性值
     */
    private static Object getFieldValueByName(String fieldName, Object o) {
        if (!StringUtils.hasText(fieldName) || o == null) {
            return null;
        }
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter);
            return method.invoke(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据属性名获取属性值
     *
     * @param fieldName
     * @param object
     * @return
     */
    private static Object getFieldValueByFieldName(String fieldName, Object object) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            //设置对象的访问权限，保证对private的属性的访问
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 反射获取  @Param注解标记list属性值
     *
     * @param primaryKeyName 主键名称
     * @param object         对象
     * @param batchFlag      batchFlag=true批量 ,batchFlag=false代表不是批量操作
     * @return
     */
    private static List<Map<String, Object>> reflectGetListValue(String primaryKeyName, Object object, boolean batchFlag) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (null == object) {
            return resultList;
        }

        //单个参数解析
        Map<String, Object> argsValueMap = new HashMap<>();
        if (!batchFlag) {
            Map<String, Object> argsMaps = beanToMap(object);
            for (String key : argsMaps.keySet()) {
                if (key.equals(primaryKeyName)) {
                    argsValueMap.put("obj", argsMaps.get(key));
                }
            }
            resultList.add(argsValueMap);
            return resultList;
        }

        //批量解析
        Map<String, Object> parameterMap = (Map<String, Object>) object;
        for (Map.Entry<String, Object> et : parameterMap.entrySet()) {
            String key = et.getKey();
            if (!key.contains("param")) {
                Object val = et.getValue();
                if (val instanceof List) {
                    // List
                    List<Object> objList = (List<Object>) et.getValue();
                    for (Object obj : objList) {
                        resultList.add(fieldListWithValueOfEt(obj, primaryKeyName));
                    }
                } else {
                    // Entity
                    Map<String, Object> argsMaps = beanToMap(val);
                    for (String tempKey : argsMaps.keySet()) {
                        if (tempKey.equals(primaryKeyName)) {
                            argsValueMap.put("obj", argsMaps.get(tempKey));
                        }
                    }
                    resultList.add(argsValueMap);
                    return resultList;
                }
            }
        }
        return resultList;
    }

    private static Map<String, Object> fieldListWithValueOfEt(Object obj, String primaryKeyName) {
        Map<String, Object> listChild = new HashMap<>();
        Class<?> clz = obj.getClass();
        Field[] declaredFields = clz.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                if (Objects.equals(primaryKeyName, field.getName())) {
                    Object value = field.get(obj);
                    listChild.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return listChild;
    }

    /**
     * bean转map
     *
     * @param bean
     * @return map
     */
    private static <T> Map<String, Object> beanToMap(T bean) {
        if (bean != null) {
            return BeanMap.create(bean);
        }
        return new HashMap<>();
    }

    /**
     * @param jdbcTemplate    操作类型
     * @param tableName       表名
     * @param primaryKeyName  主键名称
     * @param primaryKeyValue 主键值
     * @param resultList      查询结果集
     * @return
     * @desc 操作前获取数据(修改或者删除的时候需要查询操作前的数据)
     */
    private static void queryData(JdbcTemplate jdbcTemplate, String tableName, String primaryKeyName, Object primaryKeyValue, List<List<Map<String, Object>>> resultList) {
        if (StringUtils.hasText(tableName) && StringUtils.hasText(primaryKeyName) && primaryKeyValue != null) {
            String sql = " select * from " +
                tableName +
                " where " +
                primaryKeyName +
                " = ? ";
            List<Map<String, Object>> resultMaps = jdbcTemplate.queryForList(sql, primaryKeyValue);
            resultList.add(resultMaps);
        }
    }
}
