package com.yhy.jakit.jpa.starter.spec;

import javax.persistence.criteria.Predicate;

/**
 * Specification 构造器
 * <p>
 * Created on 2024-02-18 21:57
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Spec {

    static <T> PredicateBuilder<T> and() {
        return new PredicateBuilder<>(Predicate.BooleanOperator.AND);
    }

    static <T> PredicateBuilder<T> or() {
        return new PredicateBuilder<>(Predicate.BooleanOperator.OR);
    }

    static <T> PredicateBuilder<T> of() {
        return and();
    }
}
