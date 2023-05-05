package com.yhy.jakit.util.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 树形工具
 * <p>
 * 需要实现 {@link Node} 接口
 * <p>
 * Created on 2021-08-16 14:18
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Trees {

    /**
     * 从普通 List 集合转换成 Tree 结构
     *
     * @param elements         原始数据
     * @param rootPredicate    父节点的条件
     * @param elementPredicate 匹配各级节点的条件，回调参数分别是 parent, element
     * @param <T>              节点类型
     * @return 树
     * @deprecated 参考 {@link #of(List, Predicate, BiPredicate)}
     */
    @Deprecated
    public static <T extends Node<T>> List<T> fromList(List<T> elements, Predicate<T> rootPredicate, BiPredicate<T, T> elementPredicate) {
        return of(elements, rootPredicate, elementPredicate);
    }

    /**
     * 从普通 List 集合转换成 Tree 结构
     *
     * @param elements        原始数据
     * @param rootPredicate   父节点的条件
     * @param parentPredicate 匹配各级节点的条件，回调参数分别是 parent, element
     * @param <T>             节点类型
     * @return 树
     */
    public static <T extends Node<T>> List<T> of(List<T> elements, Predicate<T> rootPredicate, BiPredicate<T, T> parentPredicate) {
        if (Lists.isEmpty(elements)) {
            return elements;
        }

        List<T> result = new ArrayList<>();
        // 先选出根节点
        elements.forEach(item -> {
            if (rootPredicate.test(item)) {
                result.add(item);
            }
        });

        // 再用娃去选父节点
        result.forEach(item -> item.setChildren(buildChildren(item, elements, parentPredicate)));

        return result;
    }

    /**
     * 把 Tree 结构转换成普通 List 集合类型
     *
     * @param tree 原始数据
     * @param <T>  节点类型
     * @return 结果集
     * @deprecated 参考 {@link #flat(List)}
     */
    @Deprecated
    public static <T extends Node<T>> List<T> toList(List<T> tree) {
        return flat(tree);
    }

    /**
     * 把 Tree 结构转展开成普通 List 集合类型
     *
     * @param tree 原始数据
     * @param <T>  节点类型
     * @return 结果集
     */
    public static <T extends Node<T>> List<T> flat(List<T> tree) {
        return flat(tree, null);
    }

    /**
     * 把 Tree 结构转展开成普通 List 集合类型
     *
     * @param tree     原始数据
     * @param consumer 节点支持一些操作，回调参数分别是 parent, element
     * @param <T>      节点类型
     * @return 结果集
     */
    public static <T extends Node<T>> List<T> flat(List<T> tree, BiConsumer<T, T> consumer) {
        if (Lists.isEmpty(tree)) {
            return tree;
        }

        List<T> result = new ArrayList<>();

        for (T t : tree) {
            List<T> children = t.getChildren();
            t.setChildren(null);
            if (null != consumer && Lists.isNotEmpty(children)) {
                children.forEach(el -> consumer.accept(t, el));
            }

            result.add(t);
            children = flat(children, consumer);
            if (null != children && !children.isEmpty()) {
                result.addAll(children);
            }
        }

        return result;
    }

    /**
     * 获取子树
     *
     * @param tree      整树
     * @param predicate 子树提取条件
     * @param <T>       元素类型
     * @return 子树
     */
    public static <T extends Node<T>> List<T> pick(List<T> tree, Predicate<T> predicate) {
        if (null == tree) {
            return null;
        }
        for (T t : tree) {
            if (predicate.test(t)) {
                return Lists.of(t);
            }
            if (Lists.isNotEmpty(t.getChildren())) {
                List<T> temp = pick(t.getChildren(), predicate);
                if (Lists.isNotEmpty(temp)) {
                    return temp;
                }
            }
        }
        return null;
    }

    /**
     * 获取树的所有叶子节点
     *
     * @param tree 树
     * @param <T>  元素类型
     * @return 叶子列表
     */
    public static <T extends Node<T>> List<T> leaves(List<T> tree) {
        if (Lists.isEmpty(tree)) {
            return tree;
        }

        // 先展开，并记录父节点
        List<T> list = new ArrayList<>();
        flat(tree, (p, el) -> {
            if (Lists.isEmpty(el.getChildren())) {
                list.add(el);
            }
        });
        return list;
    }

    /**
     * 树按叶子展开成分支
     *
     * @param tree 树
     * @param <T>  元素类型
     * @return 分支列表
     */
    public static <T extends Parentable<T>> List<List<T>> branches(List<T> tree) {
        return branches(tree, null);
    }

    /**
     * 树按叶子展开成分支
     *
     * @param tree          树
     * @param pickPredicate 子树条件
     * @param <T>           元素类型
     * @return 分支列表
     */
    public static <T extends Parentable<T>> List<List<T>> branches(List<T> tree, Predicate<T> pickPredicate) {
        List<T> list = null != pickPredicate ? pick(tree, pickPredicate) : tree;
        List<T> leafList = new ArrayList<>();
        flat(list, (p, el) -> {
            el.setParent(p);
            if (Lists.isEmpty(el.getChildren())) {
                leafList.add(el);
            }
        });

        return leafList.stream().map(it -> {
            List<T> branch = Lists.of(it);
            if (null != it.getParent()) {
                T p = it;
                do {
                    p = p.getParent();
                    branch.add(p);
                } while (null != p && null != p.getParent());
            }
            return branch;
        }).collect(Collectors.toList());
    }

    /**
     * 递归造娃
     *
     * @param elements  列表元素
     * @param predicate 条件，回调参数分别是 parent, element
     * @param <T>       节点类型
     * @return 一堆娃
     */
    private static <T extends Node<T>> List<T> buildChildren(T parent, List<T> elements, BiPredicate<T, T> predicate) {
        if (Lists.isEmpty(elements)) {
            return elements;
        }

        List<T> children = new ArrayList<>();
        elements.forEach(el -> {
            if (predicate.test(parent, el)) {
                children.add(el);
            }
        });

        if (children.isEmpty()) {
            return null;
        }

        // 递归造娃，娃造娃
        for (T t : children) {
            t.setChildren(buildChildren(t, elements, predicate));
        }

        return children;
    }

    /**
     * 递归过滤树节点
     * <p>
     * 筛选满足条件的元素
     *
     * @param tree      树
     * @param predicate 条件构造器
     * @param <T>       树类型
     * @return 过滤结果
     */
    public static <T extends Node<T>> List<T> filterByElement(List<T> tree, Predicate<T> predicate) {
        return filterByElement(tree, predicate, false);
    }

    /**
     * 递归过滤树节点
     * <p>
     * 筛选满足条件的元素
     *
     * @param tree                树
     * @param predicate           条件构造器
     * @param autoPromoteChildren 自动将子节点提升，重组树结构
     * @param <T>                 树类型
     * @return 过滤结果
     */
    public static <T extends Node<T>> List<T> filterByElement(List<T> tree, Predicate<T> predicate, boolean autoPromoteChildren) {
        if (Lists.isEmpty(tree)) {
            return tree;
        }

        List<T> tempList = new ArrayList<>();
        for (T t : tree) {
            if (predicate.test(t)) {
                t.setChildren(filterByElement(t.getChildren(), predicate, autoPromoteChildren));
                tempList.add(t);
            } else if (autoPromoteChildren) {
                // 自动提升子节点级别，直接将满足条件的子节点们加入结果集
                List<T> temp = filterByElement(t.getChildren(), predicate, true);
                if (Lists.isNotEmpty(temp)) {
                    tempList.addAll(temp);
                }
            } else {
                // 不自动提升子节点时，如果有子节点满足条件，则必须将父节点添加进结果集
                List<T> temp = filterByElement(t.getChildren(), predicate, false);
                // 没有满足条件子节点时将自动舍弃当前节点及其娃们
                if (Lists.isNotEmpty(temp)) {
                    t.setChildren(temp);
                    tempList.add(t);
                }
            }
        }
        return tempList.isEmpty() ? null : tempList;
    }

    /**
     * 递归过滤树节点
     * <p>
     * 筛选满足条件元素的关系链
     *
     * @param tree           树
     * @param predicate      条件构造器
     * @param retainChildren 当前级别节点满足条件后，是否留其娃
     * @param <T>            树类型
     * @return 过滤结果
     */
    public static <T extends Node<T>> List<T> filterByPath(List<T> tree, Predicate<T> predicate, boolean retainChildren) {
        if (Lists.isEmpty(tree)) {
            return tree;
        }
        List<T> result = new ArrayList<>();
        for (T t : tree) {
            if (predicate.test(t)) {
                // 匹配到了
                if (!retainChildren) {
                    // 如果不保留其子节点，则直接置空
                    t.setChildren(null);
                }
                result.add(t);
            } else {
                // 当前层级未匹配到，则递归下一层级匹配
                t.setChildren(filterByPath(t.getChildren(), predicate, retainChildren));
                if (Lists.isNotEmpty(t.getChildren())) {
                    result.add(t);
                }
            }
        }
        return result.isEmpty() ? null : result;
    }

    /**
     * 树形元素
     * <p>
     * 使用该工具的类需要实现改接口
     *
     * @param <T> 树类型
     */
    public interface Node<T> {

        /**
         * 设置子节点们
         *
         * @param children 子节点
         */
        void setChildren(List<T> children);

        /**
         * 获取子节点们
         *
         * @return 子节点
         */
        List<T> getChildren();
    }

    /**
     * 支持父节点操作接口
     *
     * @param <T> 元素类型
     */
    public interface Parentable<T> extends Node<T> {

        /**
         * 设置父节点
         *
         * @param parent 父节点
         */
        void setParent(T parent);

        /**
         * 获取父节点
         *
         * @return 父节点
         */
        T getParent();
    }
}
