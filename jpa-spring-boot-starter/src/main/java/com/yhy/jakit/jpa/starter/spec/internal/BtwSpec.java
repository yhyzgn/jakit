package com.yhy.jakit.jpa.starter.spec.internal;

import javax.persistence.criteria.*;

/**
 * 介于 .. 之间
 * <p>
 * Created on 2024-02-18 22:25
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class BtwSpec<T> extends AbstractSpec<T> {
    private final String property;
    private final transient Comparable<Object> lower;
    private final transient Comparable<Object> upper;

    public BtwSpec(String property, Comparable<?> lower, Comparable<?> upper) {
        this.property = property;
        this.lower = (Comparable<Object>) lower;
        this.upper = (Comparable<Object>) upper;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        From<T, T> from = getRoot(property, root);
        String field = getProperty(property);
        return cb.between(from.get(field), lower, upper);
    }
}
