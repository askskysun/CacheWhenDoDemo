package com.hero.cachewhendodemo.cachewhen.bean.base;

import java.util.List;

/**
 * <pre>
 *
 * </pre>
 */
public abstract class BaseEventDataBean {
    protected List<String> idList;

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }
}
