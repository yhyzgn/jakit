package com.yhy.jakit.jpa.starter.spec.internal;

import javax.persistence.criteria.*;

/**
 * 不等于
 * <p>
 * Created on 2024-02-18 22:22
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class NeqSpec<T> extends AbstractSpec<T> {
    private final String property;
    private final transient Object[] values;

    public NeqSpec(String property, Object... values) {
        this.property = property;
        this.values = values;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        From<T, T> from = getRoot(property, root);
        String field = getProperty(property);
        if (values == null) {
            return cb.isNotNull(from.get(field));
        }
        if (values.length == 1) {
            return getPredicate(from, cb, values[0], field);
        }

        Predicate[] predicates = new Predicate[values.length];
        for (int i = 0; i < values.length; i++) {
            predicates[i] = getPredicate(root, cb, values[i], field);
        }
        return cb.or(predicates);
    }

    private Predicate getPredicate(From<T, T> root, CriteriaBuilder cb, Object value, String field) {
        return value == null ? cb.isNotNull(root.get(field)) : cb.notEqual(root.get(field), value);
    }
}
