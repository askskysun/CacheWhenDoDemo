package com.hero.cachewhendodemo.cachewhen.helper;

import androidx.annotation.NonNull;

import com.hero.cachewhendodemo.cachewhen.CacheWhenContants;
import com.hero.cachewhendodemo.cachewhen.bean.SimpleEventDataBean;
import com.hero.cachewhendodemo.cachewhen.bean.base.BaseParameterCacheBean;
import com.hero.cachewhendodemo.cachewhen.bean.SimpleParameterCacheBean;
import com.hero.cachewhendodemo.cachewhen.builder.SimpleBuilder;
import com.hero.cachewhendodemo.cachewhen.inerfaces.SimpleDoOperationInterface;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;

/**
 * <pre>
 * 简单处理类
 * 传入数据类型必须为 除了char以外的基础数据类型和String类型
 * </pre>
 * Author by sunhaihong, Email 1910713921@qq.com, Date on 2023/8/27.
 */
public class SimpleCacheWhenDaHelper<T> extends BaseCacheWhenDoHelper {
    public static SimpleBuilder getInstance() {
        return new SimpleBuilder();
    }

    public SimpleCacheWhenDaHelper(SimpleBuilder builder) {
        super(builder);
    }

    @Override
    protected SimpleBuilder getNewBuilder() {
        return new SimpleBuilder();
    }

    /**
     * @param idEvent
     * @param t       传入数据类型必须为 除了char以外的基础数据类型和String类型
     */
    public void doCacheWhen(@NonNull String idEvent, @NonNull T t) {
        SimpleParameterCacheBean<T> simpleParameterCacheBean = new SimpleParameterCacheBean(t);
        if (simpleParameterCacheBean != null) {
            doCacheWhen(idEvent, simpleParameterCacheBean);
        }
    }

    @Override
    protected void doOperation(BaseParameterCacheBean clone, List<String> copyEventIdList) {
        SimpleDoOperationInterface<T> doOperationInterface = (SimpleDoOperationInterface<T>) builderInterface.getDoOperationInterface();
        SimpleParameterCacheBean<T> cloneSimple = (SimpleParameterCacheBean) clone;
        T data = cloneSimple.getData();
        if (doOperationInterface != null) {
            doOperationInterface.doOperation(data, copyEventIdList);
        } else {
            SimpleEventDataBean<T> eventDataBean = new SimpleEventDataBean();
            eventDataBean.setData(data);
            eventDataBean.setIdList(copyEventIdList);
            LiveEventBus.get(CacheWhenContants.EventbusContants.SIMPLECACHEWHENDO_EVENT).post(eventDataBean);
        }
    }
}
