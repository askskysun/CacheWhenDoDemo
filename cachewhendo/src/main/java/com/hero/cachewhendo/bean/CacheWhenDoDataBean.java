package com.hero.cachewhendodemo.cachewhen.bean;

import com.hero.cachewhendodemo.cachewhen.bean.base.BaseParameterCacheBean;

/**
 * <pre>
 *
 * </pre>
 */
public class CacheWhenDoDataBean {

    private String id;
    private BaseParameterCacheBean data;

    public CacheWhenDoDataBean(String id, BaseParameterCacheBean data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public BaseParameterCacheBean getData() {
        return data;
    }
}