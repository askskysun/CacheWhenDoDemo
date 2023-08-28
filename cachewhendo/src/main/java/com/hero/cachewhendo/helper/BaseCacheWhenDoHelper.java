package com.hero.cachewhendodemo.cachewhen.helper;

import android.util.Log;
import androidx.annotation.NonNull;
import com.hero.cachewhendodemo.cachewhen.JsonUtils;
import com.hero.cachewhendodemo.cachewhen.bean.CacheWhenDoDataBean;
import com.hero.cachewhendodemo.cachewhen.bean.base.BaseParameterCacheBean;
import com.hero.cachewhendodemo.cachewhen.inerfaces.BuilderInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <pre>
 * 定时缓存处理帮助类
 * 用于频繁调用处理数据，但是我们只关注最新数据，又需要实时刷新，做个缓存处理：定时时间只取最新数据进行处理，每次调用位置加个id，在处理时id返回
 * 注意：1、在子线程中进行
 *      2、Builder中的默认值
 *      3、操作事件的处理接口：上下文为appcation时使用 DoOperationInterface；否则使用 LiveEventBus，防止内存泄漏
 * </pre>
 */
public abstract class BaseCacheWhenDoHelper {

    private String TAG = "CacheWhenDo";

    protected BaseCacheWhenDoHelper(BuilderInterface builderInterface) {
        TAG = getClass().getSimpleName();
        if (builderInterface != null) {
            this.builderInterface = builderInterface;
        }
        if (this.builderInterface.isDebug()) {
            Log.i(TAG, "配置builder：" + builderInterface.toString());
        }
    }

    protected abstract BuilderInterface getNewBuilder();

    protected BuilderInterface builderInterface = getNewBuilder();
    /**
     * 缓存数据 每次调用方法更新 id则保存起来 用于回调时使用
     */
    private volatile CacheWhenDoDataBean cacheWhenDoDataBean;

    /**
     * 用于记录每次调用方法的位置 用于回调时识别从哪里调用方法的
     */
    private volatile CopyOnWriteArrayList<String> eventIdList = new CopyOnWriteArrayList<>();
    private volatile ScheduledExecutorService scheduler;

    /**
     * 是否已经有启动循环任务
     */
    private AtomicBoolean isSchedulering = new AtomicBoolean(false);

    private Lock reentrantLock = new ReentrantLock();

    //读写锁
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();
    //获取写锁
    private Lock wlock = rwLock.writeLock();
    //获取读锁
    private Lock rLock = rwLock.readLock();

    protected void doCacheWhen(@NonNull String idEvent, @NonNull BaseParameterCacheBean baseParameterCacheBean) {
        if (builderInterface.isDebug()) {
            Log.i(TAG, ("进入方法 doCacheWhen  idEvent:" + idEvent));
        }

        wlock.lock();
        try {
            if (builderInterface.isDebug()) {
                Log.i(TAG, ("准备缓存数据 上个数据: " + JsonUtils.javabeanToJson(cacheWhenDoDataBean)));
            }
            //此处已经赋值变量对应关系，第二次进入方法就已经赋值
            this.cacheWhenDoDataBean = new CacheWhenDoDataBean(idEvent, baseParameterCacheBean);
            eventIdList.add(cacheWhenDoDataBean.getId());

            if (builderInterface.isDebug()) {
                Log.i(TAG, "缓存数据: " + JsonUtils.javabeanToJson(cacheWhenDoDataBean)
                        + "\n eventIdList:" + JsonUtils.javabeanToJson(eventIdList));
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            if (builderInterface.isDebug()) {
                Log.e(TAG, "进入方法 缓存 doCacheWhen", exception);
            }
        } finally {
            wlock.unlock();
        }

        start();
    }

    private void doWhen() {
        BaseParameterCacheBean clone = null;
        List<String> copyEventIdList = new ArrayList<>();
        wlock.lock();
        try {
            if (cacheWhenDoDataBean != null) {
                BaseParameterCacheBean data = cacheWhenDoDataBean.getData();
                if (data != null) {
                    //复制一份
                    clone = data.clone();
                    copyEventIdList.addAll(eventIdList);
                    if (builderInterface.isDebug()) {
                        Log.i(TAG, "每一秒钟执行  复制一份 clone: " + JsonUtils.javabeanToJson(clone)
                                + "\n copyEventIdList:" + JsonUtils.javabeanToJson(copyEventIdList));
                    }
                }
            }
            eventIdList.clear();
            cacheWhenDoDataBean = null;
        } catch (Exception exception) {
            exception.printStackTrace();
            if (builderInterface.isDebug()) {
                Log.e(TAG, "每一秒钟执行 复制并清除缓存 doWhen", exception);
            }
        } finally {
            wlock.unlock();
        }
        if (builderInterface.isDebug()) {
            Log.i(TAG, "每一秒钟执行 清除缓存 cacheWhenDoData: " + JsonUtils.javabeanToJson(cacheWhenDoDataBean)
                    + "\n eventIdList:" + JsonUtils.javabeanToJson(eventIdList));
        }

        if (clone == null || copyEventIdList.isEmpty()) {
            stop();
            if (builderInterface.isDebug()) {
                Log.i(TAG, "每一秒钟执行 无缓存数据 停止");
            }
            return;
        }
        doOperation(clone, copyEventIdList);
        if (builderInterface.isDebug()) {
            Log.i(TAG, "每一秒钟执行 执行完成或者已发送事件");
        }
    }

    protected abstract void doOperation(BaseParameterCacheBean clone, List<String> copyEventIdList);

    /**
     *
     */
    private void start() {
        reentrantLock.lock();
        try {
            //此处加锁并使用变量判断 防止 建立多个 scheduler 或者开启多次循环
            if (scheduler == null) {
                scheduler = new ScheduledThreadPoolExecutor(builderInterface.getThreadCount());
            }
            if (isSchedulering.compareAndSet(false, true)) {
                //scheduleAtFixedRate:循环执行的任务。上一个任务开始的时间开始计时，假设定义period时间为2s，那么第一个任务开始2s后，检测上一个任务是否执行完毕：
                //scheduleWithFixedDelay:循环执行的任务。以上一次任务执行时间为准，加上任务时间间隔作为下一次任务开始的时间。
                if (builderInterface.isAtFixed()) {
                    scheduler.scheduleAtFixedRate(() -> {
                        Log.i(TAG, "每一秒钟执行start: ");
                        try {
                            if (builderInterface.isDebug()) {
                                Log.i(TAG, "每一秒钟执行 start() Thread:" + Thread.currentThread() + " cacheWhenDoData ： " + JsonUtils.javabeanToJson(cacheWhenDoDataBean));
                            }
                            doWhen();
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (builderInterface.isDebug()) {
                                Log.e(TAG, "每一秒钟执行 start() scheduleAtFixedRate", e);
                            }
                        }
                    }, builderInterface.getInitialDelay(), builderInterface.getPeriod(), builderInterface.getUnit());
                    return;
                }

                scheduler.scheduleWithFixedDelay(() -> {
                    try {
                        if (builderInterface.isDebug()) {
                            Log.i(TAG, "每一秒钟执行 start() Thread:" + Thread.currentThread() + " cacheWhenDoData ： " + JsonUtils.javabeanToJson(cacheWhenDoDataBean));
                        }
                        doWhen();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (builderInterface.isDebug()) {
                            Log.e(TAG, "每一秒钟执行 start() scheduleWithFixedDelay", e);
                        }
                    }
                }, builderInterface.getInitialDelay(), builderInterface.getPeriod(), builderInterface.getUnit());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            if (builderInterface.isDebug()) {
                Log.e(TAG, " start()", exception);
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     *
     */
    public synchronized void stop() {
        if (builderInterface.isDebug()) {
            Log.i(TAG, " stop()");
        }

        reentrantLock.lock();
        try {
            isSchedulering.set(false);
            if (scheduler != null) {
                //shutdown只是将线程池的状态设置为SHUTWDOWN状态，正在执行的任务会继续执行下去，没有被执行的则中断。
                //而shutdownNow则是将线程池的状态设置为STOP，正在执行的任务则被停止，没被执行任务的则返回。
                if (builderInterface.isShutdown()) {
                    scheduler.shutdown();
                } else {
                    scheduler.shutdownNow();
                }
            }
            scheduler = null;
        } catch (Exception exception) {
            exception.printStackTrace();
            if (builderInterface.isDebug()) {
                Log.e(TAG, " stop()", exception);
            }
        } finally {
            reentrantLock.unlock();
        }
    }
}

