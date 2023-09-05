package com.hero.cachewhendodemo.rxcachewhen;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.hero.cachewhendodemo.FirstActivity;
import com.hero.cachewhendodemo.WrLockHelper;
import com.hero.cachewhendodemo.cachewhen.bean.base.BaseParameterCacheBean;
import com.hero.cachewhendodemo.cachewhen.inerfaces.BuilderInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * <pre>
 *
 * </pre>
 */
public class RxCacheWhenDoHelper<T> {

    private final String TAG = "RxCacheWhenDoHelper";
    private Builder builder = new Builder();
    private ObservableEmitter<T> emitter;
    private Disposable subscribe;
    private Observable<T> tObservable;
    private Observer<T> observer;
//    private Lock reentrantLock = new ReentrantLock();

    public static Builder getInstance() {
        return new Builder();
    }

    /**
     * 用于记录每次调用方法的位置 用于回调时识别从哪里调用方法的
     */
    private volatile CopyOnWriteArrayList<String> eventIdList = new CopyOnWriteArrayList<>();
    private WrLockHelper subscribeWrLockHelper;
    private WrLockHelper eventWrLockHelper;

    public RxCacheWhenDoHelper(Builder builder) {
        if (builder != null) {
            this.builder = builder;
        }
        if (this.builder.isDebug()) {
            Log.i(TAG, "配置builder：" + builder.toString());
        }
        subscribeWrLockHelper = new WrLockHelper();
        eventWrLockHelper = new WrLockHelper();

        tObservable = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> emitter) throws InterruptedException {
                RxCacheWhenDoHelper.this.emitter = emitter;
            }
        }).debounce(this.builder.getPeriod(), this.builder.getUnit())
                .observeOn(this.builder.getScheduler());

        observer = new Observer<T>() {
            @Override
            public void onSubscribe(@NonNull Disposable subscribe) {
                if (RxCacheWhenDoHelper.this.builder.isDebug()) {
                    Log.i(TAG, "onSubscribe: Thread:" + Thread.currentThread());
                }
                RxCacheWhenDoHelper.this.subscribe = subscribe;
            }

            @Override
            public void onNext(@NonNull T t) {
                final boolean[] disposed = {false};
                subscribeWrLockHelper.rlockDo(new WrLockHelper.OnDoInterface() {
                    @Override
                    public void onDo() {
                        if (RxCacheWhenDoHelper.this.subscribe != null) {
                            disposed[0] = RxCacheWhenDoHelper.this.subscribe.isDisposed();
                        }
                    }
                });

                if (RxCacheWhenDoHelper.this.builder.isDebug()) {
                    Log.i(TAG, "onNext:Thread:" + Thread.currentThread() + "收到处理回调，在此处进行处理，disposed:" + disposed[0] + " 数据为： " + t);
                }

                if (disposed[0]) {
                    return;
                }

                List<String> copyEventIdList = new ArrayList<>();
                eventWrLockHelper.wlockDo(new WrLockHelper.OnDoInterface() {
                    @Override
                    public void onDo() {
                        copyEventIdList.addAll(eventIdList);
                        eventIdList.clear();
                    }
                });

               /* reentrantLock.lock();
                try {
                    copyEventIdList.addAll(eventIdList);
                    eventIdList.clear();
                } catch (Exception exception) {
                    exception.printStackTrace();
                    if (RxCacheWhenDoHelper.this.builder.isDebug()) {
                        Log.e(TAG, " onNext()", exception);
                    }
                } finally {
                    reentrantLock.unlock();
                }*/

                WhenDoCallBack whenDoCallBack = RxCacheWhenDoHelper.this.builder.getWhenDoCallBack();
                if (whenDoCallBack != null) {
                    RxCacheWhenDoDataBean rxCacheWhenDoDataBean = new RxCacheWhenDoDataBean();
                    rxCacheWhenDoDataBean.setT(t);
                    rxCacheWhenDoDataBean.setEventIdList(copyEventIdList);
                    //此处如果需要不影响原数据，则在此克隆一份
                    whenDoCallBack.onNext(rxCacheWhenDoDataBean);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                if (RxCacheWhenDoHelper.this.builder.isDebug()) {
                    Log.i(TAG, "onError: Thread:" + Thread.currentThread() + " Throwable:" + e.toString());
                }

                WhenDoCallBack whenDoCallBack = RxCacheWhenDoHelper.this.builder.getWhenDoCallBack();
                if (whenDoCallBack != null) {
                    whenDoCallBack.onError(e);
                }
            }

            @Override
            public void onComplete() {
                if (RxCacheWhenDoHelper.this.builder.isDebug()) {
                    Log.i(TAG, "onComplete: Thread:" + Thread.currentThread());
                }
            }
        };

    }

    private void subscribe() {
        if (subscribe != null && !subscribe.isDisposed()) {
            return;
        }

        LifecycleOwner lifecycleOwner = this.builder.getLifecycleOwner();
        if (lifecycleOwner != null) {
            //AutoDispose的关键语句 防止内存泄漏
            tObservable.to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner)))
                    .subscribe(observer);
        } else {
            tObservable.subscribe(observer);
        }
    }

    public void doCacheWhen(@NonNull String idEvent, @NonNull T t) {
        if (builder.isDebug()) {
            Log.i(TAG, "doCacheWhen idEvent：" + idEvent + " t:" + t);
        }
        subscribe();
        eventWrLockHelper.wlockDo(new WrLockHelper.OnDoInterface() {
            @Override
            public void onDo() {
                eventIdList.add(idEvent);
            }
        });
       /* reentrantLock.lock();
        try {
            eventIdList.add(idEvent);
        } catch (Exception exception) {
            exception.printStackTrace();
            if (builder.isDebug()) {
                Log.e(TAG, " doCacheWhen()", exception);
            }
        } finally {
            reentrantLock.unlock();
        }*/

        emitter.onNext(t);
    }

    /**
     * 停止 不能停止已经发送的数据
     */
    public void stop() {
        if (builder.isDebug()) {
            Log.i(TAG, "stop()");
        }

        if (subscribe != null) {
            subscribe.dispose();
        }
    }
}
