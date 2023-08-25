package com.hero.cachewhendodemo.cachewhen;

/**
 * <pre>
 *
 * </pre>
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