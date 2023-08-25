package com.hero.cachewhendodemo.cachewhen;

import java.util.List;

/**
 * <pre>
 *
 * </pre>
 */
public class EventData {
    private ParameterCache clone;
    private List<String> idList;

    public ParameterCache getClone() {
        return clone;
    }

    public void setClone( ParameterCache clone) {
        this.clone = clone;
    }

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }
}
