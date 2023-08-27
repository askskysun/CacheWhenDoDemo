package com.hero.cachewhendodemo.cachewhen.inerfaces;

import com.hero.cachewhendodemo.cachewhen.bean.base.BaseParameterCacheBean;

import java.util.List;

/**
 * 操作事件的处理接口
 */
public interface DoOperationInterface {
    /**
     * 处理定时做的事情
     *
     * @param cloneData   复制之后的缓存数据
     * @param eventIdList 调用事件列表
     */
    void doOperation(BaseParameterCacheBean cloneData, List<String> eventIdList);
}