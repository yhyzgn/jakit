package com.yhy.jakit.util.unit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 人民币类
 * <p>
 * 人民币是中国的货币单位，由元、角、分、厘、毫五个单位组成。
 * <p>
 * 单位换算关系: 1 元 = 10 角, 1 角 = 10 分, 1 分 = 10 厘, 1 厘 = 10 毫
 * <p>
 * Created on 2024-05-13 14:29
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class RMB implements Comparable<RMB>, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 0 值
     */
    public static final RMB ZERO = new RMB(0, 0);

    /**
     * 1 元 = 10 角
     */
    public static final int JIAO_PER_YUAN = 10;

    /**
     * 1 角 = 10 分
     */
    public static final int FEN_PER_JIAO = 10;

    /**
     * 1 元 = 100 分
     */
    public static final int FEN_PER_YUAN = 100;

    /**
     * 1 元 = 1000 厘
     */
    public static final int LI_PER_YUAN = 1000;

    /**
     * 1 分 = 10 厘
     */
    public static final int LI_PER_FEN = 10;

    /**
     * 1 厘 = 10 毫
     */
    public static final int HAO_PER_LI = 10;

    /**
     * 1 分 = 100 毫
     */
    public static final int HAO_PER_FEN = 100;

    /**
     * 1 角 = 1000 毫
     */
    public static final int HAO_PER_JIAO = 1000;

    /**
     * 1 元 = 10000 毫
     */
    public static final int HAO_PER_YUAN = 10000;

    /**
     * 记录 分
     */
    private final long fen;

    /**
     * 记录 毫
     */
    private final long hao;

    /**
     * 当前金额加上指定金额
     *
     * @param amount 加入的总金额
     * @param unit   单位
     * @return 金额
     */
    public RMB plus(long amount, Unit unit) {
        Objects.requireNonNull(unit, "unit");
        if (amount <= 0) {
            return this;
        }
        switch (unit) {
            case YUAN:
                return plusYuan(amount);
            case JIAO:
                return plusJiao(amount);
            case FEN:
                return plusFen(amount);
            case LI:
                return plusLi(amount);
            case HAO:
                return plusHao(amount);
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * 当前金额加上指定元数
     *
     * @param yuan 元
     * @return RMB 实例
     */
    public RMB plusYuan(long yuan) {
        return plus(Math.multiplyExact(yuan, (long) FEN_PER_YUAN), 0);
    }

    /**
     * 当前金额加上指定角数
     *
     * @param jiao 角
     * @return RMB 实例
     */
    public RMB plusJiao(long jiao) {
        return plus(Math.multiplyExact(jiao, (long) FEN_PER_JIAO), 0);
    }

    /**
     * 当前金额加上指定分数
     *
     * @param fen 分
     * @return RMB 实例
     */
    public RMB plusFen(long fen) {
        return plus(fen, 0);
    }

    /**
     * 当前金额加上指定厘数
     *
     * @param li 厘
     * @return RMB 实例
     */
    public RMB plusLi(long li) {
        return plus(li / LI_PER_FEN, (li % LI_PER_FEN) * HAO_PER_LI);
    }

    /**
     * 当前金额加上指定毫数
     *
     * @param hao 毫
     * @return RMB 实例
     */
    public RMB plusHao(long hao) {
        return plus(0, hao);
    }

    /**
     * 当前金额加上指定金额
     *
     * @param fenAdd 加入的 分
     * @param haoAdd 加入的 毫
     * @return RMB 实例
     */
    public RMB plus(long fenAdd, long haoAdd) {
        if ((fenAdd | haoAdd) == 0) {
            return this;
        }
        fenAdd = Math.addExact(fen, fenAdd);
        fenAdd = Math.addExact(fenAdd, haoAdd / HAO_PER_FEN);
        haoAdd = haoAdd % HAO_PER_FEN;
        haoAdd = hao + haoAdd;
        return of(fenAdd, haoAdd);
    }

    /**
     * 判断总金额是否大于 0
     *
     * @return 总金额是否大于 0
     */
    public boolean isPositive() {
        return (fen | hao) > 0;
    }

    /**
     * 判断总金额是否小于 0
     *
     * @return 总金额是否小于 0
     */
    public boolean isNegative() {
        return (fen | hao) < 0;
    }

    /**
     * 判断总金额是否为 0
     *
     * @return 总金额是否为 0
     */
    public boolean isZero() {
        return (fen | hao) == 0;
    }

    /**
     * 将总金额转为 元
     *
     * @return 元 - 总额
     */
    public BigDecimal toYuan() {
        BigDecimal yuanOfHao = BigDecimal.valueOf(hao).divide(BigDecimal.valueOf(HAO_PER_YUAN), 4, RoundingMode.DOWN);
        return BigDecimal.valueOf(fen).divide(BigDecimal.valueOf(FEN_PER_YUAN), 4, RoundingMode.DOWN).add(yuanOfHao);
    }

    /**
     * 将总金额转为 角
     *
     * @return 角 - 总额
     */
    public BigDecimal toJiao() {
        BigDecimal jiaoOfHao = BigDecimal.valueOf(hao).divide(BigDecimal.valueOf(HAO_PER_JIAO), 3, RoundingMode.DOWN);
        return BigDecimal.valueOf(fen).divide(BigDecimal.valueOf(FEN_PER_JIAO), 3, RoundingMode.DOWN).add(jiaoOfHao);
    }

    /**
     * 将总金额转为 分
     *
     * @return 分 - 总额
     */
    public BigDecimal toFen() {
        BigDecimal haoOfHao = BigDecimal.valueOf(hao).divide(BigDecimal.valueOf(HAO_PER_FEN), 2, RoundingMode.DOWN);
        return BigDecimal.valueOf(fen).add(haoOfHao);
    }

    /**
     * 将总金额转为 厘
     *
     * @return 厘 - 总额
     */
    public BigDecimal toLi() {
        BigDecimal liOfFen = BigDecimal.valueOf(fen).multiply(BigDecimal.valueOf(LI_PER_FEN), MathContext.UNLIMITED);
        BigDecimal liOfHao = BigDecimal.valueOf(hao).divide(BigDecimal.valueOf(HAO_PER_LI), 1, RoundingMode.DOWN);
        return liOfFen.add(liOfHao);
    }

    /**
     * 将总金额转为 毫
     *
     * @return 毫 - 总额
     */
    public BigDecimal toHao() {
        BigDecimal liOfFen = BigDecimal.valueOf(fen).multiply(BigDecimal.valueOf(HAO_PER_FEN), MathContext.UNLIMITED);
        return BigDecimal.valueOf(hao).add(liOfFen);
    }

    @Override
    public int compareTo(@NotNull RMB other) {
        int cmp = Long.compare(fen, other.fen);
        if (cmp != 0) {
            return cmp;
        }
        return (int) (hao - other.hao);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof RMB) {
            RMB rmb = (RMB) other;
            return this.fen == rmb.fen && this.hao == rmb.hao;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) ((Long.hashCode(fen)) + (51 * hao));
    }

    /**
     * 根据 元 创建人民币金额
     *
     * @param yuanAmount 元 - 总额
     * @return RMB 实例
     */
    public static RMB ofYuan(long yuanAmount) {
        return create(Math.multiplyExact(yuanAmount, (long) FEN_PER_YUAN), 0);
    }

    /**
     * 根据 角 创建人民币金额
     *
     * @param jiaoAmount 角 - 总额
     * @return RMB 实例
     */
    public static RMB ofJiao(long jiaoAmount) {
        return create(Math.multiplyExact(jiaoAmount, (long) FEN_PER_JIAO), 0);
    }

    /**
     * 根据 分 创建人民币金额
     *
     * @param fenAmount 分 - 总额
     * @return RMB 实例
     */
    public static RMB ofFen(long fenAmount) {
        return create(fenAmount, 0);
    }

    /**
     * 根据 厘 创建人民币金额
     *
     * @param liAmount 厘 - 总额
     * @return RMB 实例
     */
    public static RMB ofLi(long liAmount) {
        long fen = liAmount / LI_PER_FEN;
        liAmount = liAmount % LI_PER_FEN;
        if (liAmount < 0) {
            liAmount += LI_PER_FEN;
            fen--;
        }
        return create(fen, liAmount * HAO_PER_LI);
    }

    /**
     * 根据 毫 创建人民币金额
     *
     * @param haoAmount 毫 - 总额
     * @return RMB 实例
     */
    public static RMB ofHao(long haoAmount) {
        long fen = haoAmount / HAO_PER_FEN;
        haoAmount = haoAmount % HAO_PER_FEN;
        if (haoAmount < 0) {
            haoAmount += HAO_PER_FEN;
            fen--;
        }
        return create(fen, haoAmount * HAO_PER_FEN);
    }

    /**
     * 根据 分 和 毫 创建人民币金额
     *
     * @param fenAmount 分 - 总额
     * @param haoAmount 毫 - 总额
     * @return RMB 实例
     */
    public static RMB of(long fenAmount, long haoAmount) {
        long fen = Math.addExact(fenAmount, Math.floorDiv(haoAmount, (long) HAO_PER_FEN));
        int hao = Math.floorMod(haoAmount, HAO_PER_FEN);
        return create(fen, hao);
    }

    /**
     * 根据 总数额 和 单位 创建人民币金额
     *
     * @param amount 总数额
     * @param unit   单位
     * @return RMB 实例
     */
    public static RMB of(long amount, Unit unit) {
        return ZERO.plus(amount, unit);
    }

    /**
     * 创建一个实例
     *
     * @param fen 分
     * @param hao 毫
     * @return RMB 实例
     */
    public static RMB create(long fen, long hao) {
        if ((fen | hao) == 0) {
            return ZERO;
        }
        return new RMB(fen, hao);
    }

    /**
     * 人民币单位
     */
    @Getter
    @AllArgsConstructor
    public enum Unit {

        /**
         * 元
         * <p>
         * 1 元 = 10 角
         */
        YUAN("YUAN", "元"),

        /**
         * 角
         * <p>
         * 1 角 = 10 分
         */
        JIAO("JIAO", "角"),

        /**
         * 分
         * <p>
         * 1 分 = 10 厘
         */
        FEN("FEN", "分"),

        /**
         * 厘
         * <p>
         * 1 厘 = 10 毫
         */
        LI("LI", "厘"),

        /**
         * 毫
         */
        HAO("HAO", "毫"),
        ;

        private final String value;
        private final String name;
    }

    public static void main(String[] args) {
        RMB rmb = RMB.ofJiao(10).plusYuan(2);
        System.out.println(rmb.toYuan());
        System.out.println(rmb.toJiao());
        System.out.println(rmb.toFen());
        System.out.println(rmb.toLi());
        System.out.println(rmb.toHao());

        System.out.println("---------------------");

        rmb = RMB.ofLi(20).plusHao(6).plusYuan(10);
        System.out.println(rmb.toYuan());
        System.out.println(rmb.toJiao());
        System.out.println(rmb.toFen());
        System.out.println(rmb.toLi());
        System.out.println(rmb.toHao());
    }
}
