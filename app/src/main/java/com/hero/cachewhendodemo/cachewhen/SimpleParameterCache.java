package com.hero.cachewhendodemo.cachewhen;

import java.lang.reflect.Field;

/**
 * 重写缓存类
 * 保存操作的数据作为属性，可以自由包装
 */
public class SimpleParameterCache<T> extends ParameterCache {

    private Integer aInteger;
    private Long aLong;
    private Double aDouble;
    private Float aFloat;
    private Short aShort;
    private Byte aByte;
    private Boolean aBoolean;
    private String aString;

    public SimpleParameterCache(Integer aInteger) {
        this.aInteger = aInteger;
    }

    public SimpleParameterCache(Long aLong) {
        this.aLong = aLong;
    }

    public SimpleParameterCache(Double aDouble) {
        this.aDouble = aDouble;
    }

    public SimpleParameterCache(Float aFloat) {
        this.aFloat = aFloat;
    }

    public SimpleParameterCache(Short aShort) {
        this.aShort = aShort;
    }

    public SimpleParameterCache(Byte aByte) {
        this.aByte = aByte;
    }

    public SimpleParameterCache(Boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public SimpleParameterCache(String aString) {
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
    public SimpleParameterCache clone() {
        if (aInteger != null) {
            Integer integerclone = Integer.valueOf(aInteger);
            SimpleParameterCache tSimpleParameterCache = new SimpleParameterCache(integerclone);
            return tSimpleParameterCache;
        }
        if (aLong != null) {
            Long aLongClone = Long.valueOf(aLong);
            SimpleParameterCache tSimpleParameterCache = new SimpleParameterCache(aLongClone);
            return tSimpleParameterCache;
        }
        if (aDouble != null) {
            Double aDoubleClone = Double.valueOf(aDouble);
            SimpleParameterCache tSimpleParameterCache = new SimpleParameterCache(aDoubleClone);
            return tSimpleParameterCache;
        }
        if (aFloat != null) {
            Float aFloatClone = Float.valueOf(aFloat);
            SimpleParameterCache tSimpleParameterCache = new SimpleParameterCache(aFloatClone);
            return tSimpleParameterCache;
        }

        if (aShort != null) {
            Short aShortClone = Short.valueOf(aShort);
            SimpleParameterCache tSimpleParameterCache = new SimpleParameterCache(aShortClone);
            return tSimpleParameterCache;
        }
        if (aByte != null) {
            Byte aByteClone = Byte.valueOf(aByte);
            SimpleParameterCache tSimpleParameterCache = new SimpleParameterCache(aByteClone);
            return tSimpleParameterCache;
        }
        if (aBoolean != null) {
            Boolean aBooleanClone = Boolean.valueOf((Boolean) aBoolean);
            SimpleParameterCache tSimpleParameterCache = new SimpleParameterCache(aBooleanClone);
            return tSimpleParameterCache;
        }
        if (aString != null) {
            String substring = new StringBuilder(aString).toString();
            SimpleParameterCache tSimpleParameterCache = new SimpleParameterCache(substring);
            return tSimpleParameterCache;
        }

        return null;
    }
}