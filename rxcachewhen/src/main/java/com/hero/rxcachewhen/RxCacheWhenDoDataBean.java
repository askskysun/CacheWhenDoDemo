package com.hero.rxcachewhen;

import java.util.List;

/**
 * <pre>
 *
 * </pre>
 */
public class RxCacheWhenDoDataBean<T> {
    private T t;
    private List<String> eventIdList;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public List<String> getEventIdList() {
        return eventIdList;
    }

    public void setEventIdList(List<String> eventIdList) {
        this.eventIdList = eventIdList;
    }
}
