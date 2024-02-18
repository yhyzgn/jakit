package com.yhy.jakit.jpa.starter.spec.internal;

import javax.persistence.criteria.*;

/**
 * 大于等于
 * <p>
 * Created on 2024-02-18 22:29
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class GeSpec<T> extends AbstractSpec<T> {
    private final String property;
    private final transient Comparable<Object> compare;

    public GeSpec(String property, Comparable<?> compare) {
        this.property = property;
        this.compare = (Comparable<Object>) compare;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        From<T, T> from = getRoot(property, root);
        String field = getProperty(property);
        return cb.greaterThanOrEqualTo(from.get(field), compare);
    }
}
