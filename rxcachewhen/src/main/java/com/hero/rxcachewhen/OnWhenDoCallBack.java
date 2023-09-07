package com.hero.rxcachewhen;

import io.reactivex.rxjava3.annotations.NonNull;

/**
 * <pre>
 *
 * </pre>
 */
public interface OnWhenDoCallBack<T> {
    void onNext(@NonNull RxCacheWhenDoDataBean<T> rxCacheWhenDoDataBean);

    void onError(@NonNull Throwable throwable);
}
