package com.yhy.jakit.starter.logging.model;

import com.yhy.jakit.starter.logging.provider.UserProvider;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志
 * <p>
 * Created on 2020-11-02 16:56
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class Log implements Serializable {
    private static final long serialVersionUID = -2345904208524663949L;

    /**
     * 日志追踪 id
     */
    private String traceId;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 操作开始时间戳 (ms)
     */
    private Long begin;

    /**
     * 操作结束时间戳 (ms)
     */
    private Long finish;

    /**
     * 操作花费时间戳 (ms)
     */
    private Long latency;

    /**
     * 通过 {@link UserProvider} 设置的用户标识
     */
    private String userIdentity;

    /**
     * 当前操作是否用到事务
     */
    private Boolean transactional;

    /**
     * 操作可能抛出的异常，发生异常时，意味着事务提交失败
     */
    private Throwable throwable;

    /**
     * 当前请求的 url
     */
    private String url;

    /**
     * 当前请求的方法
     */
    private String method;

    /**
     * 请求类型
     */
    private String contentType;

    /**
     * 当前采集日志的函数
     */
    private String function;

    /**
     * 当前采集日志函数接收到的参数值
     */
    private Object[] arguments;

    /**
     * JPA 操作记录
     */
    private List<Operation> operationList;

    public Log() {
        operationList = new ArrayList<>();
    }

    /**
     * 添加一步操作
     *
     * @param jpa 新操作
     */
    public void addOperation(Operation jpa) {
        operationList.add(jpa);
    }

    /**
     * 按操作和操作的数据类型获取最后一步操作对象
     *
     * @param type        操作
     * @param sourceClass 操作的数据类型
     * @return 操作对象
     */
    public Operation lastOperationByBeforeSource(Type type, Class<?> sourceClass) {
        Operation ops;
        for (int i = operationList.size() - 1; i >= 0; i--) {
            ops = operationList.get(i);
            if (ops.getType() == type && null != ops.getBefore() && ops.getBefore().getClass() == sourceClass) {
                return ops;
            }
        }
        return null;
    }
}
