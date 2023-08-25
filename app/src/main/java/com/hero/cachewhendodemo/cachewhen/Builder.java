package com.hero.cachewhendodemo.cachewhen;

/**
 * <pre>
 *
 * </pre>
 */

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

/**
 * 配置
 */
public class Builder {
    /**
     * 是否调试
     */
     boolean isDebug;

    /**
     * 停止方式,正在执行的任务会继续执行下去，没有被执行的则中断   false 为 shutdownNow
     */
     boolean isShutdown;

    /**
     * 循环执行是否等待上一个执行完毕  false 为 scheduleWithFixedDelay
     */
     boolean isAtFixed;

    /**
     * 循环时间
     */
     long period = 1;

    /**
     * 延迟启动时间
     */
     long initialDelay;

    /**
     * 单位
     */
     TimeUnit unit = TimeUnit.SECONDS;

    /**
     * 默认线程数
     */
     int threadCount = 3;

    /**
     * 操作事件的处理接口
     * 上下文为appcation时使用，防止内存泄漏
     */
     WeakReference<DoOperationInterface> doOperationInterfaceWeakRef;

    public Builder setDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

    /**
     * 操作事件的处理接口
     * 注意此处使用弱引用 所以不要以局部变量作为参数，否则很快被回收
     */
    public Builder setDoOperationInterface(DoOperationInterface doOperationInterface) {
        doOperationInterfaceWeakRef = new WeakReference<>(doOperationInterface);
        return this;
    }

    public Builder setShutdown(boolean shutdown) {
        isShutdown = shutdown;
        return this;
    }

    public Builder setAtFixed(boolean atFixed) {
        isAtFixed = atFixed;
        return this;
    }

    public Builder setPeriod(long period) {
        this.period = period;
        return this;
    }

    public Builder setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
        return this;
    }

    public Builder setUnit(TimeUnit unit) {
        if (unit != null) {
            this.unit = unit;
        }
        return this;
    }

    public Builder setThreadCount(int threadCount) {
        if (threadCount > 0) {
            this.threadCount = threadCount;
        }
        return this;
    }

    public CacheWhenDoHelper build() {
        return new CacheWhenDoHelper(this);
    }
}