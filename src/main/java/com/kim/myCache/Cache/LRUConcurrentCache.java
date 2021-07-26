package com.kim.myCache.Cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: MyCache
 * @description:
 * @author: Kim_yuan
 * @create: 2021-07-25 16:28
 **/

public class LRUConcurrentCache<K, V> extends LinkedHashMap<K, V> {
    private final Lock lock = new ReentrantLock();
    private int maxCapacity;
    public LRUConcurrentCache(int initialCapacity,int maxCapacity) {
        super(initialCapacity,0.75F,true);
        this.maxCapacity = maxCapacity;
    }
    @Override
    public V get(Object key) {
        lock.lock();
        try {
            return super.get(key);
        } finally {
            lock.unlock();
        }
    }
    @Override
    public V put(K key, V value) {
        lock.lock();
        try {
            return super.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        lock.lock();
        try {
            return super.remove(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxCapacity;
    }
}
