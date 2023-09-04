package com.hero.cachewhendodemo.rxcachewhen;

import io.reactivex.rxjava3.annotations.NonNull;

/**
 * <pre>
 *
 * </pre>
 */
public interface WhenDoCallBack<T> {
    void onNext(@NonNull RxCacheWhenDoDataBean<T> rxCacheWhenDoDataBean);

    void onError(@NonNull Throwable throwable);
}
