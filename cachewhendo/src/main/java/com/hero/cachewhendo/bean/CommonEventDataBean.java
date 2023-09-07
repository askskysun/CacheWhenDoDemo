package com.hero.cachewhendo.bean;

import com.hero.cachewhendo.bean.base.BaseEventDataBean;
import com.hero.cachewhendo.bean.base.BaseParameterCacheBean;

/**
 * <pre>
 *
 * </pre>
 * Author by sunhaihong, Email 1910713921@qq.com, Date on 2023/8/27.
 */
public class CommonEventDataBean extends BaseEventDataBean {

    private BaseParameterCacheBean cacheBeanClone;

    public BaseParameterCacheBean getCacheBeanClone() {
        return cacheBeanClone;
    }

    public void setCacheBeanClone(BaseParameterCacheBean cacheBeanClone) {
        this.cacheBeanClone = cacheBeanClone;
    }
}