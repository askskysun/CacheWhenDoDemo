package com.hero.cachewhendodemo;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jeremyliao.liveeventbus.LiveEventBus;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
 * Author by sunhaihong, Email 1910713921@qq.com, Date on 2023/8/1.
 */
public class CacheWhenDoHelper {
    private static final String TAG = "CacheWhenDoHelper";
    /**
     * 是否调试
     */
    private static boolean ISDEBUG = true;
    public static final String LIVEEVENTBUS_KEY = "CacheWhenDoHelper";
    private Builder builder = new Builder();

    public CacheWhenDoHelper(Builder builder) {
        if (builder != null) {
            this.builder = builder;
        }
        if (ISDEBUG) {
            Log.i(TAG, "配置builder：" + javabeanToJson(this.builder));
        }
    }

    public static Builder getInstance() {
        return new Builder();
    }

    /**
     * 缓存数据 每次调用方法更新 id则保存起来 用于回调时使用
     */
    private volatile CacheWhenDoData cacheWhenDoData;

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

    public void doCacheWhen(@NonNull String idEvent, @NonNull OnParameterCacheCallBack onParameterCacheCallBack) {
        if (ISDEBUG) {
            Log.i(TAG, ("进入方法 doCacheWhen  idEvent:" + idEvent));
        }

        wlock.lock();
        try {
            if (ISDEBUG) {
                Log.i(TAG, ("准备缓存数据 上个数据: " + javabeanToJson(cacheWhenDoData)));
            }
            //此处已经赋值变量对应关系，第二次进入方法就已经赋值
            this.cacheWhenDoData = new CacheWhenDoData(idEvent, onParameterCacheCallBack.onParameterCacheCallBack());
            eventIdList.add(cacheWhenDoData.getId());

            if (ISDEBUG) {
                Log.i(TAG, "缓存数据: " + javabeanToJson(cacheWhenDoData)
                        + "\n eventIdList:" + javabeanToJson(eventIdList));
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            if (ISDEBUG) {
                Log.e(TAG, "进入方法 缓存 doCacheWhen", exception);
            }
        } finally {
            wlock.unlock();
        }

        start();
    }

    private void doWhen() {
        ParameterCache clone = null;
        List<String> copyEventIdList = new ArrayList<>();
        wlock.lock();
        try {
            if (cacheWhenDoData != null) {
                ParameterCache data = cacheWhenDoData.getData();
                if (data != null) {
                    //复制一份
                    clone = data.clone();
                    copyEventIdList.addAll(eventIdList);
                    if (ISDEBUG) {
                        Log.i(TAG, "每一秒钟执行  复制一份 clone: " + javabeanToJson(clone)
                                + "\n copyEventIdList:" + javabeanToJson(copyEventIdList));
                    }
                }
            }
            eventIdList.clear();
            cacheWhenDoData = null;
        } catch (Exception exception) {
            exception.printStackTrace();
            if (ISDEBUG) {
                Log.e(TAG, "每一秒钟执行 复制并清除缓存 doWhen", exception);
            }
        } finally {
            wlock.unlock();
        }
        if (ISDEBUG) {
            Log.i(TAG, "每一秒钟执行 清除缓存 cacheWhenDoData: " + javabeanToJson(cacheWhenDoData)
                    + "\n eventIdList:" + javabeanToJson(eventIdList));
        }

        if (clone == null || copyEventIdList.isEmpty()) {
            stop();
            if (ISDEBUG) {
                Log.i(TAG, "每一秒钟执行 无缓存数据 停止");
            }
            return;
        }
        if (builder.doOperationInterfaceWeakRef != null && builder.doOperationInterfaceWeakRef.get() != null) {
            builder.doOperationInterfaceWeakRef.get().doOperation(clone, copyEventIdList);
        } else {
            EventData eventData = new EventData();
            eventData.setClone(clone);
            eventData.setIdList(copyEventIdList);
            LiveEventBus.get(LIVEEVENTBUS_KEY).post(eventData);
        }
        if (ISDEBUG) {
            Log.i(TAG, "每一秒钟执行 执行完成或者已发送事件");
        }
    }

    /**
     *
     */
    public void start() {
        reentrantLock.lock();
        try {
            //此处加锁并使用变量判断 防止 建立多个 scheduler 或者开启多次循环
            if (scheduler == null) {
                scheduler = new ScheduledThreadPoolExecutor(builder.threadCount);
            }
            if (isSchedulering.compareAndSet(false, true)) {
                //scheduleAtFixedRate:循环执行的任务。上一个任务开始的时间开始计时，假设定义period时间为2s，那么第一个任务开始2s后，检测上一个任务是否执行完毕：
                //scheduleWithFixedDelay:循环执行的任务。以上一次任务执行时间为准，加上任务时间间隔作为下一次任务开始的时间。
                if (builder.isAtFixed) {
                    scheduler.scheduleAtFixedRate(() -> {
                        Log.i(TAG, "每一秒钟执行start: ");
                        try {
                            if (ISDEBUG) {
                                Log.i(TAG, "每一秒钟执行 start() Thread:" + Thread.currentThread() + " cacheWhenDoData ： " + javabeanToJson(cacheWhenDoData));
                            }
                            doWhen();
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (ISDEBUG) {
                                Log.e(TAG, "每一秒钟执行 start() scheduleAtFixedRate", e);
                            }
                        }
                    }, builder.initialDelay, builder.period, builder.unit);
                    return;
                }

                scheduler.scheduleWithFixedDelay(() -> {
                    try {
                        if (ISDEBUG) {
                            Log.i(TAG, "每一秒钟执行 start() Thread:" + Thread.currentThread() + " cacheWhenDoData ： " + javabeanToJson(cacheWhenDoData));
                        }
                        doWhen();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (ISDEBUG) {
                            Log.e(TAG, "每一秒钟执行 start() scheduleWithFixedDelay", e);
                        }
                    }
                }, builder.initialDelay, builder.period, builder.unit);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            if (ISDEBUG) {
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
        if (ISDEBUG) {
            Log.i(TAG, " stop()");
        }

        reentrantLock.lock();
        try {
            isSchedulering.set(false);
            if (scheduler != null) {
                //shutdown只是将线程池的状态设置为SHUTWDOWN状态，正在执行的任务会继续执行下去，没有被执行的则中断。
                //而shutdownNow则是将线程池的状态设置为STOP，正在执行的任务则被停止，没被执行任务的则返回。
                if (builder.isShutdown) {
                    scheduler.shutdown();
                } else {
                    scheduler.shutdownNow();
                }
            }
            scheduler = null;
        } catch (Exception exception) {
            exception.printStackTrace();
            if (ISDEBUG) {
                Log.e(TAG, " stop()", exception);
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    public static String javabeanToJson(Object obj) {
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            String json = gson.toJson(obj);
            return json != null ? json : "";
        } catch (Exception e) {
            e.printStackTrace();
            if (ISDEBUG) {
                Log.e(TAG, " javabeanToJson()", e);
            }
            return "";
        }
    }

    /**
     * 操作事件的处理接口
     */
    public interface DoOperationInterface {
        /**
         * 处理定时做的事情
         *
         * @param cloneData   复制之后的缓存数据
         * @param eventIdList 事件列表
         */
        void doOperation(ParameterCache cloneData, List<String> eventIdList);
    }

    /**
     * 配置
     */
    public static class Builder {

        /**
         * 停止方式   false 为 shutdownNow
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
         * 上下文为appcation时使用，防止内存泄漏
         */
        private WeakReference<DoOperationInterface> doOperationInterfaceWeakRef;

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

    public static class CacheWhenDoData {

        private String id;
        private ParameterCache data;

        public CacheWhenDoData(String id, ParameterCache data) {
            this.id = id;
            this.data = data;
        }

        public String getId() {
            return id;
        }

        public ParameterCache getData() {
            return data;
        }
    }

    public static abstract class ParameterCache implements Cloneable {
        @Override
        public abstract ParameterCache clone();
    }

    public interface OnParameterCacheCallBack {
        ParameterCache onParameterCacheCallBack();
    }

    public static class EventData {
        private ParameterCache clone;
        private List<String> idList;

        public ParameterCache getClone() {
            return clone;
        }

        public void setClone(ParameterCache clone) {
            this.clone = clone;
        }

        public List<String> getIdList() {
            return idList;
        }

        public void setIdList(List<String> idList) {
            this.idList = idList;
        }
    }
}

