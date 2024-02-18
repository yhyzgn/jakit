package com.yhy.jakit.jpa.starter.spec.internal;

import javax.persistence.criteria.*;

/**
 * 小于
 * <p>
 * Created on 2024-02-18 22:29
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class LtSpec<T> extends AbstractSpec<T> {
    private final String property;
    private final transient Comparable<Object> compare;

    public LtSpec(String property, Comparable<?> compare) {
        this.property = property;
        this.compare = (Comparable<Object>) compare;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        From<T, T> from = getRoot(property, root);
        String field = getProperty(property);
        return cb.lessThan(from.get(field), compare);
    }
}
