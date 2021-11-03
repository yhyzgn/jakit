package com.yhy.jakit.ioc;

/**
 * Bean 工厂
 * <p>
 * 提供 IOC 容器的基本功能
 * <p>
 * Created on 2021-10-09 16:20
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public interface BeanFactory {

    <T> T getBean(String name);

    <T> T getBean(Class<T> type);
}
