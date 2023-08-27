package com.hero.cachewhendodemo.cachewhen.bean;

import com.hero.cachewhendodemo.cachewhen.bean.base.BaseParameterCacheBean;

import java.lang.reflect.Field;

/**
 * 重写缓存类
 * 保存操作的数据作为属性，可以自由包装
 */
public class SimpleParameterCacheBean<T> extends BaseParameterCacheBean {

    private Integer aInteger;
    private Long aLong;
    private Double aDouble;
    private Float aFloat;
    private Short aShort;
    private Byte aByte;
    private Boolean aBoolean;
    private String aString;

    public SimpleParameterCacheBean(Integer aInteger) {
        this.aInteger = aInteger;
    }

    public SimpleParameterCacheBean(Long aLong) {
        this.aLong = aLong;
    }

    public SimpleParameterCacheBean(Double aDouble) {
        this.aDouble = aDouble;
    }

    public SimpleParameterCacheBean(Float aFloat) {
        this.aFloat = aFloat;
    }

    public SimpleParameterCacheBean(Short aShort) {
        this.aShort = aShort;
    }

    public SimpleParameterCacheBean(Byte aByte) {
        this.aByte = aByte;
    }

    public SimpleParameterCacheBean(Boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public SimpleParameterCacheBean(String aString) {
        this.aString = aString;
    }

    /**
     * 泛型要与操作参数类型一致 CacheWhenDoHelper.doCacheWhen
     *
     * @return
     */
    public T getData() {
        try {
            Class clazz = this.getClass();
            //能获取该类中所有的属性，但是不能获取父类的属性
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                //设置即使该属性是private，也可以进行访问(默认是false)
                field.setAccessible(true);
                Object value = field.get(this);
                if (value == null) {
                    continue;
                }
                T valueT = ((T) value);
                return valueT;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 此类一定要实现  复制一份数据，不影响原数据
     *
     * @return
     */
    @Override
    public SimpleParameterCacheBean clone() {
        if (aInteger != null) {
            Integer integerclone = Integer.valueOf(aInteger);
            SimpleParameterCacheBean tSimpleParameterCacheBean = new SimpleParameterCacheBean(integerclone);
            return tSimpleParameterCacheBean;
        }
        if (aLong != null) {
            Long aLongClone = Long.valueOf(aLong);
            SimpleParameterCacheBean tSimpleParameterCacheBean = new SimpleParameterCacheBean(aLongClone);
            return tSimpleParameterCacheBean;
        }
        if (aDouble != null) {
            Double aDoubleClone = Double.valueOf(aDouble);
            SimpleParameterCacheBean tSimpleParameterCacheBean = new SimpleParameterCacheBean(aDoubleClone);
            return tSimpleParameterCacheBean;
        }
        if (aFloat != null) {
            Float aFloatClone = Float.valueOf(aFloat);
            SimpleParameterCacheBean tSimpleParameterCacheBean = new SimpleParameterCacheBean(aFloatClone);
            return tSimpleParameterCacheBean;
        }

        if (aShort != null) {
            Short aShortClone = Short.valueOf(aShort);
            SimpleParameterCacheBean tSimpleParameterCacheBean = new SimpleParameterCacheBean(aShortClone);
            return tSimpleParameterCacheBean;
        }
        if (aByte != null) {
            Byte aByteClone = Byte.valueOf(aByte);
            SimpleParameterCacheBean tSimpleParameterCacheBean = new SimpleParameterCacheBean(aByteClone);
            return tSimpleParameterCacheBean;
        }
        if (aBoolean != null) {
            Boolean aBooleanClone = Boolean.valueOf((Boolean) aBoolean);
            SimpleParameterCacheBean tSimpleParameterCacheBean = new SimpleParameterCacheBean(aBooleanClone);
            return tSimpleParameterCacheBean;
        }
        if (aString != null) {
            String substring = new StringBuilder(aString).toString();
            SimpleParameterCacheBean tSimpleParameterCacheBean = new SimpleParameterCacheBean(substring);
            return tSimpleParameterCacheBean;
        }

        return null;
    }
}