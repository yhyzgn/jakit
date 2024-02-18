package com.yhy.jakit.jpa.starter.spec.internal;

import javax.persistence.criteria.*;

/**
 * 模糊查询
 * <p>
 * Created on 2024-02-18 22:39
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class NlkSpec<T> extends AbstractSpec<T> {
    private final String property;
    private final String[] patterns;

    public NlkSpec(String property, String... patterns) {
        this.property = property;
        this.patterns = patterns;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        From<T, T> from = getRoot(property, root);
        String field = getProperty(property);
        if (patterns.length == 1) {
            return cb.like(from.get(field), patterns[0]).not();
        }
        Predicate[] predicates = new Predicate[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            predicates[i] = cb.like(from.get(field), patterns[i]).not();
        }
        return cb.or(predicates);
    }
}
