package com.kim.myCache.Cache;

import com.kim.myCache.Entities.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public interface Cache<K,V> {
    Logger logger = LoggerFactory.getLogger(Cache.class);
    Boolean add(K key,V value);

    Boolean add(K key, V value, Long expireTime, TimeUnit unit);

    V get(K key);


}
