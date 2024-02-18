package com.yhy.jakit.jpa.starter.spec.internal;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.io.Serializable;

/**
 * Created on 2024-02-18 22:10
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractSpec<T> implements Specification<T>, Serializable {

    public String getProperty(String property) {
        if (null != property && property.contains(".")) {
            return StringUtils.split(property, ".")[1];
        }
        return property;
    }

    public From<T, T> getRoot(String property, Root<T> root) {
        if (property.contains(".")) {
            String joinProperty = StringUtils.split(property, ".")[0];
            return root.join(joinProperty, JoinType.LEFT);
        }
        return root;
    }
}
