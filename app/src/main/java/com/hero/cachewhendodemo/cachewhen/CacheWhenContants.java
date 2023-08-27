package com.hero.cachewhendodemo.cachewhen;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *
 * </pre>
 */
public class CacheWhenContants {
    public static class EventbusContants{
        public static final String SIMPLECACHEWHENDO_EVENT = "SimpleCacheWhenDoEvent";
        public static final String COMMONCACHEWHENDO_EVENT = "CommonCacheWhenDoEvent";
    }

    @StringDef({Type.SIMPLE_TYPE, Type.COMMON_TYPE}) //限制入参范围
    @Retention(RetentionPolicy.SOURCE) //设置注解保留级别为源码阶段
    public @interface Type {
        String SIMPLE_TYPE = "SIMPLE_TYPE";
        String COMMON_TYPE = "COMMON_TYPE";
    }
}
