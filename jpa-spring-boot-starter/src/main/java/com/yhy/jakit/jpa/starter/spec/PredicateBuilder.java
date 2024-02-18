package com.yhy.jakit.jpa.starter.spec;

import com.yhy.jakit.jpa.starter.spec.internal.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * 条件构造器
 * <p>
 * Created on 2024-02-18 22:04
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class PredicateBuilder<T> {
    private final Predicate.BooleanOperator operator;
    private final List<Specification<T>> specificationList;

    PredicateBuilder(Predicate.BooleanOperator operator) {
        this.operator = operator;
        this.specificationList = new ArrayList<>();
    }

    public PredicateBuilder<T> eq(String property, Object... values) {
        return eq(true, property, values);
    }

    public PredicateBuilder<T> eq(boolean condition, String property, Object... values) {
        return predicate(condition, new EqSpec<>(property, values));
    }

    public PredicateBuilder<T> ne(String property, Object... values) {
        return ne(true, property, values);
    }

    public PredicateBuilder<T> ne(boolean condition, String property, Object... values) {
        return predicate(condition, new NeqSpec<>(property, values));
    }

    public PredicateBuilder<T> gt(String property, Comparable<?> compare) {
        return gt(true, property, compare);
    }

    public PredicateBuilder<T> gt(boolean condition, String property, Comparable<?> compare) {
        return predicate(condition, new GtSpec<>(property, compare));
    }

    public PredicateBuilder<T> ge(String property, Comparable<?> compare) {
        return ge(true, property, compare);
    }

    public PredicateBuilder<T> ge(boolean condition, String property, Comparable<?> compare) {
        return predicate(condition, new GeSpec<>(property, compare));
    }

    public PredicateBuilder<T> lt(String property, Comparable<?> number) {
        return lt(true, property, number);
    }

    public PredicateBuilder<T> lt(boolean condition, String property, Comparable<?> compare) {
        return predicate(condition, new LtSpec<>(property, compare));
    }

    public PredicateBuilder<T> le(String property, Comparable<?> compare) {
        return le(true, property, compare);
    }

    public PredicateBuilder<T> le(boolean condition, String property, Comparable<?> compare) {
        return predicate(condition, new LeSpec<>(property, compare));
    }

    public PredicateBuilder<T> btw(String property, Comparable<?> lower, Comparable<?> upper) {
        return btw(true, property, lower, upper);
    }

    public PredicateBuilder<T> btw(boolean condition, String property, Comparable<?> lower, Comparable<?> upper) {
        return predicate(condition, new BtwSpec<>(property, lower, upper));
    }

    public PredicateBuilder<T> lk(String property, String... patterns) {
        return lk(true, property, patterns);
    }

    public PredicateBuilder<T> lk(boolean condition, String property, String... patterns) {
        return predicate(condition, new LkSpec<>(property, patterns));
    }

    public PredicateBuilder<T> nlk(String property, String... patterns) {
        return nlk(true, property, patterns);
    }

    public PredicateBuilder<T> nlk(boolean condition, String property, String... patterns) {
        return predicate(condition, new NlkSpec<>(property, patterns));
    }

    public PredicateBuilder<T> in(String property, Collection<?> values) {
        return in(true, property, values);
    }

    public PredicateBuilder<T> in(boolean condition, String property, Collection<?> values) {
        return predicate(condition, new InSpec<>(property, values));
    }

    public PredicateBuilder<T> nin(String property, Collection<?> values) {
        return nin(true, property, values);
    }

    public PredicateBuilder<T> nin(boolean condition, String property, Collection<?> values) {
        return predicate(condition, new NinSpec<>(property, values));
    }

    public PredicateBuilder<T> contains(String property, String... examples) {
        return contains(true, property, examples);
    }

    public PredicateBuilder<T> contains(boolean condition, String property, String... examples) {
        String[] patterns = Arrays.stream(examples).map(it -> "%" + it + "%").toArray(value -> new String[0]);
        return lk(condition, property, patterns);
    }

    public PredicateBuilder<T> startWith(String property, String... examples) {
        return startWith(true, property, examples);
    }

    public PredicateBuilder<T> startWith(boolean condition, String property, String... examples) {
        String[] patterns = Arrays.stream(examples).map(it -> it + "%").toArray(value -> new String[0]);
        return lk(condition, property, patterns);
    }

    public PredicateBuilder<T> endWith(String property, String... examples) {
        return endWith(true, property, examples);
    }

    public PredicateBuilder<T> endWith(boolean condition, String property, String... examples) {
        String[] patterns = Arrays.stream(examples).map(it -> "%" + it).toArray(value -> new String[0]);
        return lk(condition, property, patterns);
    }

    public PredicateBuilder<T> notContains(String property, String... examples) {
        return notContains(true, property, examples);
    }

    public PredicateBuilder<T> notContains(boolean condition, String property, String... examples) {
        String[] patterns = Arrays.stream(examples).map(it -> "%" + it + "%").toArray(value -> new String[0]);
        return nlk(condition, property, patterns);
    }

    public PredicateBuilder<T> notStartWith(String property, String... examples) {
        return notStartWith(true, property, examples);
    }

    public PredicateBuilder<T> notStartWith(boolean condition, String property, String... examples) {
        String[] patterns = Arrays.stream(examples).map(it -> it + "%").toArray(value -> new String[0]);
        return nlk(condition, property, patterns);
    }

    public PredicateBuilder<T> notEndWith(String property, String... examples) {
        return notEndWith(true, property, examples);
    }

    public PredicateBuilder<T> notEndWith(boolean condition, String property, String... examples) {
        String[] patterns = Arrays.stream(examples).map(it -> "%" + it).toArray(value -> new String[0]);
        return nlk(condition, property, patterns);
    }

    public PredicateBuilder<T> predicate(Specification<T> specification) {
        return predicate(true, specification);
    }

    public PredicateBuilder<T> predicate(boolean condition, Specification<T> specification) {
        if (condition) {
            this.specificationList.add(specification);
        }
        return this;
    }

    public Specification<T> build() {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate[] predicates = new Predicate[specificationList.size()];
            for (int i = 0; i < specificationList.size(); i++) {
                predicates[i] = specificationList.get(i).toPredicate(root, query, cb);
            }
            if (Objects.equals(predicates.length, 0)) {
                return null;
            }
            return Predicate.BooleanOperator.OR.equals(operator) ? cb.or(predicates) : cb.and(predicates);
        };
    }
}
