package com.kim.myCache.Cache;

public interface Cache<K,V> {
    Boolean add(K key,V value);

    Boolean add(K key,V value, Long expireTime);

    Entry get(K key);


}
