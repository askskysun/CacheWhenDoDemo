package com.hero.cachewhendodemo.cachewhen;

/**
 * <pre>
 *
 * </pre>
 * Author by sunhaihong, Email 1910713921@qq.com, Date on 2023/8/25.
 */

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
    void doOperation(ParameterCache cloneData, List<String> eventIdList);
}