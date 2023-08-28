package com.hero.cachewhendo.inerfaces;

import java.util.List;

/**
 * 操作事件的处理接口
 */
public interface SimpleDoOperationInterface<T> extends BaseDoOperationInterface {
    /**
     * 处理定时做的事情
     *
     * @param t   复制之后的缓存数据
     * @param eventIdList 调用事件列表
     */
    void doOperation(T t, List<String> eventIdList);
}