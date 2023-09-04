package com.hero.cachewhendodemo.rxcachewhen;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import com.hero.cachewhendodemo.cachewhen.helper.CommonCacheWhenDoHelper;
import com.hero.cachewhendodemo.cachewhen.inerfaces.BaseDoOperationInterface;

import java.lang.ref.WeakReference;
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
    private long period = 1;

    /**
     * 单位
     */
    private TimeUnit unit = TimeUnit.SECONDS;

    /**
     * 操作事件的处理接口
     */
    private WeakReference<WhenDoCallBack> doWhenDoCallBackWeakRef;

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

    public WhenDoCallBack getWhenDoCallBack() {
        try {
            if (doWhenDoCallBackWeakRef != null) {
                return doWhenDoCallBackWeakRef.get();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            if (isDebug) {
                Log.e(TAG, "getWhenDoCallBack", exception);
            }
        }

        return null;
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
     * 注意此处使用弱引用 所以不要以局部变量作为参数，否则很快被回收
     */
    public Builder setWhenDoCallBack(WhenDoCallBack whenDoCallBack) {
        if (whenDoCallBack != null) {
            doWhenDoCallBackWeakRef = new WeakReference<>(whenDoCallBack);
        }
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