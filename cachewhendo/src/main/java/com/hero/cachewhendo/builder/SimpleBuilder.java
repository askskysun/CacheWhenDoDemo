package com.hero.cachewhendo.builder;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import com.hero.cachewhendo.helper.SimpleCacheWhenDaHelper;
import com.hero.cachewhendo.inerfaces.BaseDoOperationInterface;
import com.hero.cachewhendo.inerfaces.BuilderInterface;
import com.hero.cachewhendo.inerfaces.SimpleDoOperationInterface;
import java.util.concurrent.TimeUnit;
import io.reactivex.rxjava3.core.Scheduler;

/**
 * <pre>
 * 简单数据类型的配置
 * 使用组合代替继承  装饰者模式  需要原始类和装饰类需要继承统一个抽象类或者实现统一个接口
 * </pre>
 */
public class SimpleBuilder implements BuilderInterface {

    private static final String TAG = "SimpleBuilder";

    private Builder builder;

    public SimpleBuilder() {
        builder = new Builder();
    }

    @Override
    public boolean isDebug() {
        return builder.isDebug();
    }

    @Override
    public LifecycleOwner getLifecycleOwner() {
        return builder.getLifecycleOwner();
    }

    @Override
    public boolean isShutdown() {
        return builder.isShutdown();
    }

    @Override
    public boolean isAtFixed() {
        return builder.isAtFixed();
    }

    @Override
    public long getPeriod() {
        return builder.getPeriod();
    }

    @Override
    public long getInitialDelay() {
        return builder.getInitialDelay();
    }

    @Override
    public TimeUnit getUnit() {
        return builder.getUnit();
    }

    @Override
    public int getThreadCount() {
        return builder.getThreadCount();
    }

    public SimpleBuilder setDebug(boolean isDebug) {
        builder.setDebug(isDebug);
        return this;
    }

    public SimpleBuilder setShutdown(boolean shutdown) {
        builder.setShutdown(shutdown);
        return this;
    }

    public SimpleBuilder setAtFixed(boolean atFixed) {
        builder.setAtFixed(atFixed);
        return this;
    }

    public SimpleBuilder setPeriod(long period) {
        builder.setPeriod(period);
        return this;
    }

    public SimpleBuilder setInitialDelay(long initialDelay) {
        builder.setInitialDelay(initialDelay);
        return this;
    }

    public SimpleBuilder setUnit(TimeUnit unit) {
        builder.setUnit(unit);
        return this;
    }

    public SimpleBuilder setThreadCount(int threadCount) {
        builder.setThreadCount(threadCount);
        return this;
    }

    public SimpleBuilder setScheduler(Scheduler scheduler) {
        builder.setScheduler(scheduler);
        return this;
    }
    /**
     * 操作事件的处理接口
     */
    public SimpleBuilder setDoOperationInterface(SimpleDoOperationInterface doOperationInterface) {
        builder.setDoOperationInterface(doOperationInterface);
        return this;
    }

    @Override
    public SimpleDoOperationInterface getDoOperationInterface() {
        try {
            BaseDoOperationInterface doOperationInterface = builder.getDoOperationInterface();
            if (doOperationInterface != null) {
                return (SimpleDoOperationInterface) doOperationInterface;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            if (isDebug()) {
                Log.e(TAG, "getDoOperationInterface", exception);
            }
        }

        return null;
    }

    @Override
    public Scheduler getScheduler() {
        return builder.getScheduler();
    }

    public SimpleCacheWhenDaHelper build() {
        return new SimpleCacheWhenDaHelper(this);
    }

    @Override
    public String toString() {
        return "SimpleBuilder{" +
                "isDebug=" + isDebug() +
                ", isShutdown=" + isShutdown() +
                ", isAtFixed=" + isAtFixed() +
                ", getPeriod=" + getPeriod() +
                ", getInitialDelay=" + getInitialDelay() +
                ", getUnit=" + getUnit() +
                ", getThreadCount=" + getThreadCount() +
                ", getDoOperationInterface=" + (getDoOperationInterface() == null ? "null" : getDoOperationInterface().toString()) +
                ", getScheduler=" + (getScheduler() == null ? "null" : getScheduler().toString()) +
                '}';
    }
}
