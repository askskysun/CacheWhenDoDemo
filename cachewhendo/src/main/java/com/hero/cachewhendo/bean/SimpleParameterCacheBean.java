package com.hero.cachewhendo.bean;

import androidx.annotation.NonNull;
import com.hero.cachewhendo.bean.base.BaseParameterCacheBean;

/**
 * 重写缓存类
 * 传入数据类型必须为 除了char以外的基础数据类型和String类型
 * 保存操作的数据作为属性，可以自由包装
 */
public class SimpleParameterCacheBean<T> extends BaseParameterCacheBean {

    private T t;

    public SimpleParameterCacheBean(@NonNull T t) {
        if (!(t instanceof Integer)
                && !(t instanceof Long)
                && !(t instanceof Double)
                && !(t instanceof Float)
                && !(t instanceof Short)
                && !(t instanceof Byte)
                && !(t instanceof Boolean)
                && !(t instanceof String)) {
            throw new ClassCastException("非指定类型");
        }

        this.t = t;
    }

    /**
     * 泛型要与操作参数类型一致 CacheWhenDoHelper.doCacheWhen
     *
     * @return
     */
    public T getData() {
        return t;
    }

    /**
     * 此类一定要实现  复制一份数据，不影响原数据
     *
     * @return
     */
    @Override
    public SimpleParameterCacheBean clone() {
        if (t instanceof Integer) {
            Integer integerclone = Integer.valueOf((Integer) t);
            SimpleParameterCacheBean tSimpleParameterCacheBean = new SimpleParameterCacheBean(integerclone);
            return tSimpleParameterCacheBean;
        }
        if (t instanceof Long) {
            Long aLongClone = Long.valueOf((Long) t);
            SimpleParameterCacheBean tSimpleParameterCacheBean = new SimpleParameterCacheBean(aLongClone);
            return tSimpleParameterCacheBean;
        }
        if (t instanceof Double) {
            Double aDoubleClone = Double.valueOf((Double) t);
            SimpleParameterCacheBean tSimpleParameterCacheBean = new SimpleParameterCacheBean(aDoubleClone);
            return tSimpleParameterCacheBean;
        }
        if (t instanceof Float) {
            Float aFloatClone = Float.valueOf((Float) t);
            SimpleParameterCacheBean tSimpleParameterCacheBean = new SimpleParameterCacheBean(aFloatClone);
            return tSimpleParameterCacheBean;
        }

        if (t instanceof Short) {
            Short aShortClone = Short.valueOf((Short) t);
            SimpleParameterCacheBean tSimpleParameterCacheBean = new SimpleParameterCacheBean(aShortClone);
            return tSimpleParameterCacheBean;
        }
        if (t instanceof Byte) {
            Byte aByteClone = Byte.valueOf((Byte) t);
            SimpleParameterCacheBean tSimpleParameterCacheBean = new SimpleParameterCacheBean(aByteClone);
            return tSimpleParameterCacheBean;
        }
        if (t instanceof Boolean) {
            Boolean aBooleanClone = Boolean.valueOf((Boolean) t);
            SimpleParameterCacheBean tSimpleParameterCacheBean = new SimpleParameterCacheBean(aBooleanClone);
            return tSimpleParameterCacheBean;
        }
        if (t instanceof String) {
            String substring = new StringBuilder((String) t).toString();
            SimpleParameterCacheBean tSimpleParameterCacheBean = new SimpleParameterCacheBean(substring);
            return tSimpleParameterCacheBean;
        }

        return null;
    }
}