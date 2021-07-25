package com.kim.myCache.Entities;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class  Entry<K,V> implements Serializable {
    private static final long serialVersionUID = 1918373817726811488L;
    private K key;
    private V value;
    private Long expireTime;
    private Long createTime;
    private TimeUnit unit;

    public Entry() {

    }

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
        this.createTime = System.currentTimeMillis();
    }

    public Entry(K key, V value, Long expireTime, TimeUnit unit) {
        this.key = key;
        this.value = value;
        this.expireTime = expireTime;
        this.createTime = System.currentTimeMillis();
        this.unit = unit;
    }

    public Boolean isExpire() {
        return System.currentTimeMillis() - unit.toMillis(expireTime)> createTime;
    }
    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
