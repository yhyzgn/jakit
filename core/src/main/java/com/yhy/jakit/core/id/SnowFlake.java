package com.yhy.jakit.core.id;

/**
 * 雪花算法生成分布式ID
 * <p>
 * Created on 2021-01-29 10:42
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class SnowFlake {
    // 开始时间截 (2020-01-01)
    private final static long START_TIMESTAMP = 1577808000000L;

    // 机器id所占的位数
    private final static long WORKER_ID_BITS = 5L;

    // 数据标识id所占的位数
    private final static long DATA_CENTER_ID_BITS = 5L;

    // 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
    private final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    // 支持的最大数据标识id，结果是31
    private final static long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

    // 序列在id中占的位数
    private final static long SEQUENCE_BITS = 12L;

    // 机器ID向左移12位
    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;

    // 数据标识id向左移17位(12+5)
    private final static long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    //时间截向左移22位(5+5+12)
    private final static long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    // 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
    private final static long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    // 工作机器ID(0~31)
    private final long workerId;

    // 数据中心ID(0~31)
    private final long dataCenterId;

    // 毫秒内序列(0~4095)
    private long sequence = 0L;

    // 上次生成ID的时间截
    private long lastTimestamp = -1L;

    /**
     * 构造函数
     *
     * @param workerId     机房id
     * @param dataCenterId 数据中心id
     */
    private SnowFlake(long workerId, long dataCenterId) {
        // 参数检验
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("DataCenter Id can't be greater than %d or less than 0", MAX_DATA_CENTER_ID));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * 获取实例
     *
     * @param workerId     机房id
     * @param dataCenterId 数据中心id
     * @return 实例
     */
    public static SnowFlake create(long workerId, long dataCenterId) {
        return new SnowFlake(workerId, dataCenterId) {
        };
    }

    /**
     * 创建id (该方法是线程安全的)
     *
     * @return 创建的id
     */
    public String nextStr() {
        return next() + "";
    }

    /**
     * 创建id (该方法是线程安全的)
     *
     * @return 创建的id
     */
    public synchronized long next() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }

        // 上次生成ID的时间截
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_LEFT_SHIFT) | (dataCenterId << DATA_CENTER_ID_SHIFT) | (workerId << WORKER_ID_SHIFT) | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        SnowFlake sf = SnowFlake.create(12, 12);
        for (int i = 0; i < 20; i++) {
            System.out.println(sf.nextStr());
        }
    }
}