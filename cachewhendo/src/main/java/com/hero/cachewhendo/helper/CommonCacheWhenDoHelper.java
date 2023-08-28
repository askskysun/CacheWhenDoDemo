package com.hero.cachewhendodemo.cachewhen.helper;

import androidx.annotation.NonNull;

import com.hero.cachewhendodemo.cachewhen.CacheWhenContants;
import com.hero.cachewhendodemo.cachewhen.bean.CommonEventDataBean;
import com.hero.cachewhendodemo.cachewhen.bean.base.BaseParameterCacheBean;
import com.hero.cachewhendodemo.cachewhen.builder.Builder;
import com.hero.cachewhendodemo.cachewhen.inerfaces.CommonDoOperationInterface;
import com.hero.cachewhendodemo.cachewhen.inerfaces.OnCreateParameterCache;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;

/**
 * <pre>
 * 定时缓存处理帮助类
 * 用于频繁调用处理数据，但是我们只关注最新数据，又需要实时刷新，做个缓存处理：定时时间只取最新数据进行处理，每次调用位置加个id，在处理时id返回
 * 注意：1、在子线程中进行
 *      2、Builder中的默认值
 *      3、操作事件的处理接口：上下文为appcation时使用 DoOperationInterface；否则使用 LiveEventBus，防止内存泄漏
 * </pre>
 */
public class CommonCacheWhenDoHelper extends BaseCacheWhenDoHelper {

    public static Builder getInstance() {
        return new Builder();
    }

    public CommonCacheWhenDoHelper(Builder builder) {
        super(builder);
    }

    public void doCacheWhen(@NonNull String idEvent, @NonNull OnCreateParameterCache onCreateParameterCache) {
        doCacheWhen(idEvent, onCreateParameterCache.onCreateParameterCache());
    }

    @Override
    protected Builder getNewBuilder() {
        return new Builder();
    }

    @Override
    protected void doOperation(BaseParameterCacheBean clone, List<String> copyEventIdList) {
        Scheduler scheduler = builderInterface.getScheduler();
        if (scheduler == null) {
            doOperationRun(clone, copyEventIdList);
            return;
        }
        //使用rxjava切换到主线程
        Observable.just(0)
                .observeOn(scheduler)
                .subscribe(integer -> {
                    doOperationRun(clone, copyEventIdList);
                });
    }

    private void doOperationRun(BaseParameterCacheBean clone, List<String> copyEventIdList) {
        CommonDoOperationInterface doOperationInterface = (CommonDoOperationInterface) builderInterface.getDoOperationInterface();
        if (doOperationInterface != null) {
            doOperationInterface.doOperation(clone, copyEventIdList);
        } else {
            CommonEventDataBean eventDataBean = new CommonEventDataBean();
            eventDataBean.setCacheBeanClone(clone);
            eventDataBean.setIdList(copyEventIdList);
            LiveEventBus.get(CacheWhenContants.EventbusContants.COMMONCACHEWHENDO_EVENT).post(eventDataBean);
        }
    }
}

