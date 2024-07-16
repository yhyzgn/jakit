package com.yhy.jakit.util.unit;

import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 百分比单位封装
 * <p>
 * 内部保存流转的值为百分数解构后的值，如10%，则内部保存值为0.1, 100%，则内部保存值为1.0, 0.01%，则内部保存值为0.001。
 * <p>
 * Created on 2024-07-02 14:31
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
public class Percent implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 零值
     */
    private static final Percent ZERO = new Percent(BigDecimal.ZERO);

    /**
     * 值
     * <p>
     * 此处只保留缩小 100 倍的百分数值，如 10%，则内部保存值为 0.1, 100%，则内部保存值为 1.0, 0.01%，则内部保存值为 0.001
     */
    private BigDecimal value;

    /**
     * 构造方法
     *
     * @param value 百分数值, 直接字面量取值即可, 内部自动缩小 100 倍
     */
    private Percent(BigDecimal value) {
        this.value = value;
    }

    /**
     * 乘法计算
     *
     * @param number 另一个因数
     * @return 结果
     */
    public BigDecimal multiply(BigDecimal number) {
        return value.multiply(number);
    }

    /**
     * 加法计算
     *
     * @param number 另一个加数
     * @return 结果
     */
    public Percent add(String number) {
        return add(Percent.of(number));
    }

    /**
     * 加法计算
     *
     * @param number 另一个加数
     * @return 结果
     */
    public Percent add(BigDecimal number) {
        return add(Percent.of(number));
    }

    /**
     * 加法计算
     *
     * @param number 另一个加数
     * @return 结果
     */
    public Percent add(Percent number) {
        value = value.add(number.value);
        return this;
    }

    @Override
    public String toString() {
        return value.movePointRight(2).setScale(2, RoundingMode.HALF_UP) + "%";
    }

    /**
     * 静态方法，用于快速创建 Percent 对象
     *
     * @param value 百分数值, 直接字面量取值即可, 内部自动缩小 100 倍
     * @return Percent 对象
     */
    public static Percent of(double value) {
        return of(BigDecimal.valueOf(value));
    }

    /**
     * 静态方法，用于快速创建 Percent 对象
     *
     * @param value 百分数值, 直接字面量取值即可, 内部自动缩小 100 倍
     * @return Percent 对象
     */
    public static Percent of(String value) {
        if (value == null || value.isEmpty()) {
            return ZERO;
        }
        if (value.endsWith("%")) {
            value = value.substring(0, value.length() - 1);
        }
        return of(new BigDecimal(value));
    }

    /**
     * 静态方法，用于快速创建 Percent 对象
     *
     * @param value 百分数值, 直接字面量取值即可, 内部自动缩小 100 倍
     * @return Percent 对象
     */
    public static Percent of(BigDecimal value) {
        return new Percent(value.movePointLeft(2));
    }
}
