package com.hero.cachewhendodemo.cachewhen.bean;

import com.hero.cachewhendodemo.cachewhen.bean.base.BaseParameterCacheBean;

import java.util.List;

/**
 * <pre>
 *
 * </pre>
 */
public class EventDataBean {
    private BaseParameterCacheBean clone;
    private List<String> idList;

    public BaseParameterCacheBean getClone() {
        return clone;
    }

    public void setClone( BaseParameterCacheBean clone) {
        this.clone = clone;
    }

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }
}
