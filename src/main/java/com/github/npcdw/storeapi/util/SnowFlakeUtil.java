package com.github.npcdw.storeapi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnowFlakeUtil {
    private static final Logger log = LoggerFactory.getLogger(SnowFlakeUtil.class);

    private static final long workerId = 1;
    private static final long datacenterId = 1;

    private static long sequence = 0;

    // 开始时间的时间戳（毫秒）
    private static final long twepoch = 1577808000000L;
    // 服务器标识一共几位
    private static final long workerIdBits = 5L;
    private static final long maxWorkerId = ~(-1L << workerIdBits);
    // 数据中心标识一共几位
    private static final long datacenterIdBits = 5L;
    private static final long maxDatacenterId = ~(-1L << datacenterIdBits);
    // 序列号一共几位
    private static final long sequenceBits = 12L;

    // 服务器标识偏移量
    private static final long workerIdShift = sequenceBits;
    // 数据中心标识偏移量
    private static final long datacenterIdShift = sequenceBits + workerIdBits;
    // 时间戳偏移量
    private static final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    // 保证序列号命中在 0 ~ 2的序列号位数次方 内
    private static final long sequenceMask = ~(-1L << sequenceBits);
    // 最后一次时间戳
    private static long lastTimestamp = -1L;

    public static synchronized long nextId() {
        long timestamp = timeGen();
        // 如果生成的时间戳比之前最后一次生成的时间戳小，说明时间被回调了
        if (timestamp < lastTimestamp) {
            log.error("clock is moving backwards.  Rejecting requests until {}.", lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift) |
            (datacenterId << datacenterIdShift) |
            (workerId << workerIdShift) |
            sequence;
    }

    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private static long timeGen() {
        return System.currentTimeMillis();
    }

}
