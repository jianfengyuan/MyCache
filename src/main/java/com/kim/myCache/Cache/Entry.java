package com.kim.myCache.Cache;

import java.io.Serializable;

public class Entry implements Serializable {
    private static final long serialVersionUID = 1918373817726811488L;
    private String key;
    private String value;
    private Long expireTime;
    private Long createTime;

    public Entry(String key, String value) {
        this.key = key;
        this.value = value;
        this.createTime = System.currentTimeMillis();
    }

    public Entry(String key, String value, Long expireTime) {
        this.key = key;
        this.value = value;
        this.expireTime = expireTime;
        this.createTime = System.currentTimeMillis();
    }

    public Boolean isExpire() {
        return System.currentTimeMillis() - expireTime > createTime;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
