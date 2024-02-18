package com.yhy.jakit.jpa.starter.spec.internal;

import javax.persistence.criteria.*;
import java.util.Collection;

/**
 * 不在
 * <p>
 * Created on 2024-02-18 22:33
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class NinSpec<T> extends AbstractSpec<T> {
    private final String property;
    private final transient Collection<?> values;

    public NinSpec(String property, Collection<?> values) {
        this.property = property;
        this.values = values;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        From<T, T> from = getRoot(property, root);
        String field = getProperty(property);
        return from.get(field).in(values).not();
    }
}
