package com.hero.cachewhendodemo.cachewhen.inerfaces;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Scheduler;

/**
 * <pre>
 *
 * </pre>
 * Author by sunhaihong, Email 1910713921@qq.com, Date on 2023/8/27.
 */
public interface BuilderInterface {

    /**
     * 是否调试
     */
    boolean isDebug();

    /**
     * 停止方式,正在执行的任务会继续执行下去，没有被执行的则中断   false 为 shutdownNow
     */
    boolean isShutdown();

    /**
     * 循环执行是否等待上一个执行完毕  false 为 scheduleWithFixedDelay
     */
    boolean isAtFixed();

    /**
     * 循环时间
     */
    long getPeriod();

    /**
     * 延迟启动时间
     */
    long getInitialDelay();

    /**
     * 单位
     */
    TimeUnit getUnit();

    /**
     * 线程数
     */
    int getThreadCount();

    /**
     * 操作事件的处理接口
     * @return
     */
    BaseDoOperationInterface getDoOperationInterface();

    /**
     * 处理线程
     * @return
     */
    Scheduler getScheduler();

    @Override
    String toString();
}
