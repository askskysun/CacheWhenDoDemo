package com.hero.cachewhendodemo.cachewhen.bean;

import com.hero.cachewhendodemo.cachewhen.bean.base.BaseEventDataBean;

/**
 * <pre>
 *
 * </pre>
 * Author by sunhaihong, Email 1910713921@qq.com, Date on 2023/8/27.
 */
public class SimpleEventDataBean<T> extends BaseEventDataBean {

    private T t;

    public T getData() {
        return t;
    }

    public void setData(T t) {
        this.t = t;
    }
}