package com.hero.cachewhendodemo.cachewhen;

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
 * Author by sun, Email 1910713921@qq.com, Date on 2023/8/1.
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
            Log.i(TAG, "配置builder：" + JsonUtils.javabeanToJson(this.builder));
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

    /**
     * 执行操作
     *
     * @param idEvent  操作事件的id，记录执行操作的位置，操作回调会返回此id
     * @param byteData 创建缓存数据，作为结果返回
     */
    public void doCacheWhen(@NonNull String idEvent, @NonNull Byte byteData) {
        doCacheWhenCommon(idEvent, byteData);
    }

    /**
     * 执行操作
     *
     * @param idEvent   操作事件的id，记录执行操作的位置，操作回调会返回此id
     * @param shortData 创建缓存数据，作为结果返回
     */
    public void doCacheWhen(@NonNull String idEvent, @NonNull Short shortData) {
        doCacheWhenCommon(idEvent, shortData);
    }

    /**
     * 执行操作
     *
     * @param idEvent     操作事件的id，记录执行操作的位置，操作回调会返回此id
     * @param integerData 创建缓存数据，作为结果返回
     */
    public void doCacheWhen(@NonNull String idEvent, @NonNull Integer integerData) {
        doCacheWhenCommon(idEvent, integerData);
    }

    /**
     * 执行操作
     *
     * @param idEvent  操作事件的id，记录执行操作的位置，操作回调会返回此id
     * @param longData 创建缓存数据，作为结果返回
     */
    public void doCacheWhen(@NonNull String idEvent, @NonNull Long longData) {
        doCacheWhenCommon(idEvent, longData);
    }

    /**
     * 执行操作
     *
     * @param idEvent   操作事件的id，记录执行操作的位置，操作回调会返回此id
     * @param floatData 创建缓存数据，作为结果返回
     */
    public void doCacheWhen(@NonNull String idEvent, @NonNull Float floatData) {
        doCacheWhenCommon(idEvent, floatData);
    }

    /**
     * 执行操作
     *
     * @param idEvent    操作事件的id，记录执行操作的位置，操作回调会返回此id
     * @param doubleData 创建缓存数据，作为结果返回
     */
    public void doCacheWhen(@NonNull String idEvent, @NonNull Double doubleData) {
        doCacheWhenCommon(idEvent, doubleData);
    }

    /**
     * 执行操作
     *
     * @param idEvent     操作事件的id，记录执行操作的位置，操作回调会返回此id
     * @param booleanData 创建缓存数据，作为结果返回
     */
    public void doCacheWhen(@NonNull String idEvent, @NonNull Boolean booleanData) {
        doCacheWhenCommon(idEvent, booleanData);
    }

    /**
     * 执行操作
     *
     * @param idEvent     操作事件的id，记录执行操作的位置，操作回调会返回此id
     * @param stringData 创建缓存数据，作为结果返回
     */
    public void doCacheWhen(@NonNull String idEvent, @NonNull String stringData) {
        doCacheWhenCommon(idEvent, stringData);
    }

    public void doCacheWhen(@NonNull String idEvent, @NonNull OnCreateParameterCache onCreateParameterCache) {
        doCacheWhenCommon(idEvent, onCreateParameterCache);
    }

    private void doCacheWhenCommon(@NonNull String idEvent, @NonNull Object object) {
        SimpleParameterCache simpleParameterCache = null;
        if (object instanceof Integer) {
            simpleParameterCache = new SimpleParameterCache((Integer) object);
        } else if (object instanceof Long) {
            simpleParameterCache = new SimpleParameterCache((Long) object);
        } else if (object instanceof Double) {
            simpleParameterCache = new SimpleParameterCache((Double) object);
        } else if (object instanceof Float) {
            simpleParameterCache = new SimpleParameterCache((Float) object);
        }  else if (object instanceof Short) {
            simpleParameterCache = new SimpleParameterCache((Short) object);
        } else if (object instanceof Byte) {
            simpleParameterCache = new SimpleParameterCache((Byte) object);
        } else if (object instanceof Boolean) {
            simpleParameterCache = new SimpleParameterCache((Boolean) object);
        } else if (object instanceof String) {
            simpleParameterCache = new SimpleParameterCache((String) object);
        } else if (object instanceof OnCreateParameterCache) {
            doCacheWhen(idEvent, ((OnCreateParameterCache) object).onCreateParameterCache());
        }
        if (simpleParameterCache != null) {
            doCacheWhen(idEvent, simpleParameterCache);
        }
    }

    private void doCacheWhen(@NonNull String idEvent, @NonNull ParameterCache parameterCache) {
        if (ISDEBUG) {
            Log.i(TAG, ("进入方法 doCacheWhen  idEvent:" + idEvent));
        }

        wlock.lock();
        try {
            if (ISDEBUG) {
                Log.i(TAG, ("准备缓存数据 上个数据: " + JsonUtils.javabeanToJson(cacheWhenDoData)));
            }
            //此处已经赋值变量对应关系，第二次进入方法就已经赋值
            this.cacheWhenDoData = new CacheWhenDoData(idEvent, parameterCache);
            eventIdList.add(cacheWhenDoData.getId());

            if (ISDEBUG) {
                Log.i(TAG, "缓存数据: " + JsonUtils.javabeanToJson(cacheWhenDoData)
                        + "\n eventIdList:" + JsonUtils.javabeanToJson(eventIdList));
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
                        Log.i(TAG, "每一秒钟执行  复制一份 clone: " + JsonUtils.javabeanToJson(clone)
                                + "\n copyEventIdList:" + JsonUtils.javabeanToJson(copyEventIdList));
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
            Log.i(TAG, "每一秒钟执行 清除缓存 cacheWhenDoData: " + JsonUtils.javabeanToJson(cacheWhenDoData)
                    + "\n eventIdList:" + JsonUtils.javabeanToJson(eventIdList));
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
                                Log.i(TAG, "每一秒钟执行 start() Thread:" + Thread.currentThread() + " cacheWhenDoData ： " + JsonUtils.javabeanToJson(cacheWhenDoData));
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
                            Log.i(TAG, "每一秒钟执行 start() Thread:" + Thread.currentThread() + " cacheWhenDoData ： " + JsonUtils.javabeanToJson(cacheWhenDoData));
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
}

