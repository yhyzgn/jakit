package com.yhy.jakit.starter.logging.mybatis.interceptor;

import com.yhy.jakit.starter.logging.model.Type;
import com.yhy.jakit.starter.logging.mybatis.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created on 2022-06-14 17:05
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 * <p>
 * method 对应的 update 包括了最常用的 insert/update/delete 三种操作，因此 update 本身无法直接判断sql为何种执行过程。
 */
@Slf4j
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
@Component
public class LoggingInterceptor implements Interceptor {
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            // SQL 执行语句
            Object[] args = invocation.getArgs();
            MappedStatement stat = (MappedStatement) args[0];
            Configuration conf = stat.getConfiguration();

            // 参数，可能是实体，也可能是个 map，比如自己用 @Param 注入参数，那就是 map 对象
            Object param = null;
            if (args.length > 1) {
                param = invocation.getArgs()[1];
            }

            BoundSql boundSql = null;
            if (null != param) {
                boundSql = stat.getBoundSql(param);
            }

            Type opType = SqlUtils.opType(stat);

            // 获取修改器前的值
            List<List<Map<String, Object>>> resultMaps = SqlUtils.getDataBeforeOperate(stat, boundSql, param, jdbcTemplate);
            // 执行结果
            Object returnValue = invocation.proceed();
            // 获取sql语句
            String sql = SqlUtils.showSql(conf, boundSql);

            log.info("Sql :{}", sql);
            log.info("修改前数据 :{}", resultMaps);
            log.info("修改后数据 :{}", param);

            // TODO 待实现~

            return returnValue;
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
