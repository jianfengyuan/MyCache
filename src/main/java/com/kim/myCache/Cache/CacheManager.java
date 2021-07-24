package com.kim.myCache.Cache;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ConcurrentHashMap;

public class CacheManager<K,V> implements Cache<K,V>, InitializingBean, DisposableBean {

    @Override
    public Boolean add(K key, V value) {
        return null;
    }

    @Override
    public Boolean add(K key, V value, Long expireTime) {
        return null;
    }

    @Override
    public Entry get(K key) {
        return null;
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
