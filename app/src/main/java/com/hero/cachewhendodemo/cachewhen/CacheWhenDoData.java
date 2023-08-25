package com.hero.cachewhendodemo.cachewhen;

/**
 * <pre>
 *
 * </pre>
 * Author by sunhaihong, Email 1910713921@qq.com, Date on 2023/8/25.
 */
public class CacheWhenDoData {

    private String id;
    private ParameterCache data;

    public CacheWhenDoData(String id, ParameterCache data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public ParameterCache getData() {
        return data;
    }
}