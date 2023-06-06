package com.yhy.jakit.util;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 日期工具类
 * <p>
 * Created on 2022-11-06 03:06
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public interface LocalDateTimeUtils {

    /**
     * LocalDateTime 转 毫秒
     *
     * @param ldt LocalDateTime 实例
     * @return 毫秒数
     */
    static long millisOf(LocalDateTime ldt) {
        return null == ldt ? 0 : Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()).getTime();
    }

    /**
     * LocalDateTime 转 毫秒
     *
     * @param ld LocalDateTime 实例
     * @return 毫秒数
     */
    static long millisOf(LocalDate ld) {
        return null == ld ? 0 : Date.from(ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime();
    }

    /**
     * 毫秒 转 LocalDateTime
     *
     * @param millis 毫秒数
     * @return LocalDateTime 实例
     */
    @NotNull
    static LocalDateTime fromMillis(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }

    /**
     * 当前时间戳
     *
     * @return 当前时间戳
     */
    static long millisOfNow() {
        return millisOf(LocalDateTime.now());
    }

    /**
     * 格式化
     *
     * @param time      时间
     * @param formatter 格式
     * @return 结果
     */
    static String format(LocalDateTime time, DateTimeFormatter formatter) {
        return formatter.format(time);
    }

    /**
     * 格式化
     *
     * @param time    时间
     * @param pattern 格式
     * @return 结果
     */
    static String format(LocalDateTime time, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(time);
    }

    /**
     * 返回两个日期中的每一天
     *
     * @param begin 开始日期
     * @param end   截止日期
     * @return 日期列表
     */
    static List<LocalDate> daysBetween(LocalDate begin, LocalDate end) {
        if (null == begin) {
            begin = LocalDate.now();
        }
        if (null == end) {
            end = LocalDate.now();
        }
        List<LocalDate> list = new ArrayList<>();
        while (!end.isBefore(begin)) {
            list.add(begin);
            begin = begin.plusDays(1);
        }
        return list;
    }
}
