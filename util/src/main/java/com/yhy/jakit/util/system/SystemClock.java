package com.yhy.jakit.util.system;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 系统时钟
 * <p>
 * 高并发场景下System.currentTimeMillis()的性能问题的优化
 * System.currentTimeMillis()的调用比new一个普通对象要耗时的多（具体耗时高出多少我还没测试过，有人说是100倍左右）
 * System.currentTimeMillis()之所以慢是因为去跟系统打了一次交道 后台定时更新时钟，JVM退出时，线程自动回收
 * <p>
 * see： <a href="https://git.oschina.net/yu120/sequence">sequence</a>
 * <p>
 * Created on 2020-10-30 14:23
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class SystemClock {
    // 时间更新间隔，单位：ms
    private final long period;

    // 现在的时刻
    private volatile long now;

    /**
     * 构造函数
     *
     * @param period 时钟更新间隔，单位毫秒
     */
    public SystemClock(long period) {
        this.period = period;
        this.now = System.currentTimeMillis();
        scheduleClockUpdating();
    }

    /**
     * 开启计时器线程
     */
    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "System Clock");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(() -> now = System.currentTimeMillis(), period, period, TimeUnit.MILLISECONDS);
    }

    /**
     * @return 当前时间毫秒数
     */
    private long currentTimeMillis() {
        return now;
    }

    /*
     * ------------------------------------------------------------------------
     * static
     * ------------------------------------------------------------------------
     */

    /**
     * 单例
     */
    private static class InstanceHolder {
        public static final SystemClock INSTANCE = new SystemClock(1);
    }

    /**
     * 单例实例
     *
     * @return 单例实例
     */
    private static SystemClock instance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 当前时间戳 ms
     *
     * @return 当前时间
     */
    public static long now() {
        return instance().currentTimeMillis();
    }

    /**
     * 当前时间格式化
     *
     * @param pattern 时间格式
     * @return 当前时间格式化
     */
    public static String now(String pattern) {
        return new SimpleDateFormat(pattern).format(nowDate());
    }

    /**
     * 当前日期
     *
     * @return 当前日期
     */
    public static Date nowDate() {
        return new Date(now());
    }

    /**
     * 当前时间
     *
     * @return 当前时间
     */
    public static LocalDateTime nowTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(now()), ZoneId.systemDefault());
    }
}
