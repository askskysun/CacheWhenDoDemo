package com.hero.cachewhendodemo.rxcachewhen;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.hero.cachewhendodemo.FirstActivity;
import com.hero.cachewhendodemo.cachewhen.bean.base.BaseParameterCacheBean;
import com.hero.cachewhendodemo.cachewhen.inerfaces.BuilderInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    private Lock reentrantLock = new ReentrantLock();

    public static Builder getInstance() {
        return new Builder();
    }

    /**
     * 用于记录每次调用方法的位置 用于回调时识别从哪里调用方法的
     */
    private volatile CopyOnWriteArrayList<String> eventIdList = new CopyOnWriteArrayList<>();

    public RxCacheWhenDoHelper(Builder builder) {
        if (builder != null) {
            this.builder = builder;
        }
        if (this.builder.isDebug()) {
            Log.i(TAG, "配置builder：" + builder.toString());
        }

        Observable<T> tObservable = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> emitter) throws InterruptedException {
                RxCacheWhenDoHelper.this.emitter = emitter;
            }
        }).debounce(this.builder.getPeriod(), this.builder.getUnit())
                .observeOn(this.builder.getScheduler());

        LifecycleOwner lifecycleOwner = this.builder.getLifecycleOwner();

        Observer<T> observer = new Observer<T>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                if (RxCacheWhenDoHelper.this.builder.isDebug()) {
                    Log.i(TAG, "onSubscribe: Thread:" + Thread.currentThread());
                }
                RxCacheWhenDoHelper.this.subscribe = subscribe;
            }

            @Override
            public void onNext(@NonNull T t) {
                if (RxCacheWhenDoHelper.this.builder.isDebug()) {
                    Log.i(TAG, "onNext:Thread:" + Thread.currentThread() + "收到处理回调，在此处进行处理，数据为： " + t);
                }
                List<String> copyEventIdList = new ArrayList<>();
                reentrantLock.lock();
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
                }

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

        //AutoDispose的关键语句 防止内存泄漏
        if (lifecycleOwner != null) {
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

        reentrantLock.lock();
        try {
            eventIdList.add(idEvent);
        } catch (Exception exception) {
            exception.printStackTrace();
            if (builder.isDebug()) {
                Log.e(TAG, " doCacheWhen()", exception);
            }
        } finally {
            reentrantLock.unlock();
        }

        emitter.onNext(t);
    }

    public void stop() {
        if (builder.isDebug()) {
            Log.i(TAG, "stop()");
        }

        if (subscribe != null) {
            subscribe.dispose();
        }
    }
}
