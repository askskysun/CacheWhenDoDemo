package com.hero.rxcachewhen;

import androidx.lifecycle.LifecycleOwner;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 配置
 */
public class Builder {

    private static final String TAG = "Builder";

    /**
     * 是否调试 执行时打印对应日志
     */
    private boolean isDebug;

    /**
     * 循环时间
     */
    private long period = 1000;

    /**
     * 单位
     */
    private TimeUnit unit = TimeUnit.MILLISECONDS;

    /**
     * 操作事件的处理接口
     */
    private OnWhenDoCallBack onWhenDoCallBack;

    /**
     * 执行线程 默认当前线程
     * 主线程：AndroidSchedulers.mainThread()
     */
    private Scheduler scheduler = Schedulers.trampoline();
    private LifecycleOwner owner;

    public boolean isDebug() {
        return isDebug;
    }

    public long getPeriod() {
        return period;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public OnWhenDoCallBack getWhenDoCallBack() {
        return onWhenDoCallBack;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public LifecycleOwner getLifecycleOwner() {
        return owner;
    }

    public Builder setDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

    /**
     * 操作事件的处理接口
     */
    public Builder setWhenDoCallBack(OnWhenDoCallBack onWhenDoCallBack) {
        this.onWhenDoCallBack = onWhenDoCallBack;
        return this;
    }

    public Builder setPeriod(long period) {
        if (period > 0) {
            this.period = period;
        }
        return this;
    }

    public Builder setUnit(TimeUnit unit) {
        if (unit != null) {
            this.unit = unit;
        }
        return this;
    }

    public Builder setScheduler(Scheduler scheduler) {
        if (scheduler != null) {
            this.scheduler = scheduler;
        }
        return this;
    }

    /**
     * 内部已有判断  可以传空值
     * @param owner
     * @return
     */
    public Builder setLifecycleOwner(LifecycleOwner owner) {
        this.owner = owner;
        return this;
    }

    public RxCacheWhenDoHelper builder() {
        return new RxCacheWhenDoHelper(this);
    }

    @Override
    public String toString() {
        return "Builder{" +
                "isDebug=" + isDebug +
                ", period=" + period +
                ", unit=" + unit +
                ", scheduler=" + (scheduler == null ? "null" : scheduler.toString()) +
                '}';
    }
}