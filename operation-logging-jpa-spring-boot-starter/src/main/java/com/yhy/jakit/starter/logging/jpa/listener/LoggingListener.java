package com.yhy.jakit.starter.logging.jpa.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.yhy.jakit.starter.logging.holder.LogHolder;
import com.yhy.jakit.starter.logging.model.Log;
import com.yhy.jakit.starter.logging.model.Operation;
import com.yhy.jakit.starter.logging.model.Type;
import com.yhy.jakit.util.system.SystemClock;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.persistence.*;

/**
 * 自定义 jpa entity 的操作监听器
 * <p>
 * Created on 2021-07-08 10:25
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class LoggingListener implements ApplicationContextAware {
    private final ObjectMapper objectMapper;
    private ApplicationContext context;

    public LoggingListener() {
        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        objectMapper.findAndRegisterModules();
    }

    /**
     * 保存之前调用
     *
     * @param source 保存前的记录
     */
    @PrePersist
    public void prePersist(Object source) {
        before(Type.Insert, source);
    }

    /**
     * 保存之后调用
     *
     * @param source 保存后的记录
     */
    @PostPersist
    public void postPersist(Object source) {
        after(Type.Insert, source);
    }

    /**
     * 更新之前调用
     *
     * @param source 更新前的记录
     */
    @PreUpdate
    public void preUpdate(Object source) {
        before(Type.Update, source);
    }

    /**
     * 更新之后调用
     *
     * @param source 更新后的记录
     */
    @PostUpdate
    public void postUpdate(Object source) {
        after(Type.Update, source);
    }

    /**
     * 删除之前调用
     *
     * @param source 删除前的记录
     */
    @PreRemove
    public void preRemove(Object source) {
        before(Type.Delete, source);
    }

    /**
     * 删除之后调用
     *
     * @param source 删除后的记录
     */
    @PostRemove
    public void postRemove(Object source) {
        after(Type.Delete, source);
    }

    /**
     * 查询之后调用
     *
     * @param source 查询后的记录
     */
    @PostLoad
    public void postLoad(Object source) {
        after(Type.Select, source);
    }

    private void before(Type type, Object source) {
        Log log = LogHolder.get();
        if (null != log) {
            Operation ops = new Operation();
            ops.setType(type);
            ops.setBefore(clone(source));
            ops.setTransactional(type != Type.Select); // 是否通过事务提交？需探究有效解决方案，此处先默认
            ops.setBegin(SystemClock.now());
            log.addOperation(ops);
            LogHolder.set(log);
        }
    }

    private void after(Type type, Object source) {
        Log log = LogHolder.get();
        if (null != log) {
            // 获取该操作变更前的操作对象
            Operation ops = log.lastOperationByBeforeSource(type, source.getClass());
            if (null != ops) {
                ops.setAfter(clone(source));
                ops.setFinish(SystemClock.now());
            }
            LogHolder.set(log);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T clone(T src) {
        try {
            String json = objectMapper.writeValueAsString(src);
            return objectMapper.readValue(json, (Class<T>) src.getClass());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
