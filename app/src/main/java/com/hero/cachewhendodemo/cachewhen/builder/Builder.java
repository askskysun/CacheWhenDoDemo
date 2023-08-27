package com.hero.cachewhendodemo.cachewhen.builder;

import android.util.Log;

import com.hero.cachewhendodemo.cachewhen.helper.CommonCacheWhenDoHelper;
import com.hero.cachewhendodemo.cachewhen.inerfaces.BaseDoOperationInterface;
import com.hero.cachewhendodemo.cachewhen.inerfaces.BuilderInterface;
import com.hero.cachewhendodemo.cachewhen.inerfaces.CommonDoOperationInterface;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

/**
 * 配置
 */
public class Builder implements BuilderInterface {
    private static final String TAG = "Builder";

    /**
     * 是否调试 执行时打印对应日志
     */
    private boolean isDebug;

    /**
     * 停止方式,正在执行的任务会继续执行下去，没有被执行的则中断   false 为 shutdownNow
     */
    private boolean isShutdown;

    /**
     * 循环执行是否等待上一个执行完毕  false 为 scheduleWithFixedDelay
     */
    private boolean isAtFixed;

    /**
     * 循环时间
     */
    private long period = 1;

    /**
     * 延迟启动时间
     */
    private long initialDelay;

    /**
     * 单位
     */
    private TimeUnit unit = TimeUnit.SECONDS;

    /**
     * 默认线程数
     */
    private int threadCount = 3;

    /**
     * 操作事件的处理接口
     */
    protected WeakReference<BaseDoOperationInterface> doOperationInterfaceWeakRef;

    @Override
    public boolean isDebug() {
        return isDebug;
    }

    @Override
    public boolean isShutdown() {
        return isShutdown;
    }

    @Override
    public boolean isAtFixed() {
        return isAtFixed;
    }

    @Override
    public long getPeriod() {
        return period;
    }

    @Override
    public long getInitialDelay() {
        return initialDelay;
    }

    @Override
    public TimeUnit getUnit() {
        return unit;
    }

    @Override
    public int getThreadCount() {
        return threadCount;
    }

    @Override
    public BaseDoOperationInterface getDoOperationInterface() {
        try {
            if (doOperationInterfaceWeakRef != null) {
                return doOperationInterfaceWeakRef.get();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            if (isDebug) {
                Log.e(TAG, "getDoOperationInterface", exception);
            }
        }

        return null;
    }

    public Builder setDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

    /**
     * 操作事件的处理接口
     * 注意此处使用弱引用 所以不要以局部变量作为参数，否则很快被回收
     */
    public Builder setDoOperationInterface(BaseDoOperationInterface doOperationInterface) {
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

    public CommonCacheWhenDoHelper build() {
        return new CommonCacheWhenDoHelper(this);
    }
}