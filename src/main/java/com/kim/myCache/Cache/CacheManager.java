package com.kim.myCache.Cache;

import com.kim.myCache.Entities.Entry;
import com.kim.myCache.Utils.ThreadPoolUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CacheManager<K,V> implements Cache<K,V>, InitializingBean, DisposableBean {
    private static final long MAX_ACTIVE_EXPIRE_CYCLE_LOOKUPS_PER_LOOP = 1<<30;
    private long countThreadsHold=50;
    private Map<K, Entry<K,V>> dataMap;
    private ScheduledExecutorService scheduler;


    private void init() {
        dataMap = new ConcurrentHashMap<>();
        initExpireScheduler();
    }

    private void initExpireScheduler() {
        scheduler = ThreadPoolUtils.createScheduledThread();
        scheduler.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                while (true) {
                    if (!expireCheckAndRemove()) {
                        break;
                    }
                    countThreadsHold = countThreadsHold <= MAX_ACTIVE_EXPIRE_CYCLE_LOOKUPS_PER_LOOP?
                            countThreadsHold * 2:MAX_ACTIVE_EXPIRE_CYCLE_LOOKUPS_PER_LOOP;
                    Cache.logger.info("delete expired entry again...");
                }
                countThreadsHold = countThreadsHold <= 50 ? 50 : countThreadsHold / 2;
            }
        },1,1,TimeUnit.SECONDS);
    }

    private Boolean expireCheckAndRemove() {
        int size = dataMap.size();
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        Cache.logger.info("current cache size: " + size);
        Cache.logger.info("current countThreadsHold: " + countThreadsHold);
        if (size ==0) {
            return false;
        }
        int count = 0;
        int expire_count = 0;
        List<String> expireList = new ArrayList<>();
        Object[] keys = dataMap.keySet().toArray();
        while (count < countThreadsHold && count < size){
            count++;
            int i = random.nextInt(size);
            String key = keys[i].toString();
            Entry<K, V> kvEntry = dataMap.get(key);
            if (kvEntry.isExpire()) {
                expireList.add(key);
                expire_count++;
            }
        }
        Cache.logger.info(expire_count + " entries expired");
        for (String k :
                expireList) {
//            Cache.logger.info("deleting entry");
//            Cache.logger.info("key:" + k + " expired!");
            dataMap.remove(k);
        }
        return expire_count > countThreadsHold / 4;
    }

    @Override
    public Boolean add(K key, V value) {
        add(key, value, 10L, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public Boolean add(K key, V value, Long expireTime, TimeUnit unit) {
        Entry<K,V> e = new Entry<>(key, value, expireTime, unit);
        dataMap.put(key, e);
        return true;
    }

    @Override
    public V get(K key) {
        Entry<K, V> e;
        e = dataMap.get(key);
        if (e==null || e.isExpire()) {
            dataMap.remove(key);
            return null;
        }
        return e.getValue();
    }

    @Override
    public void destroy() throws Exception {
        logger.info("cache cleaning");
        scheduler.shutdown();
        while (true) {
            if (scheduler.isTerminated()) {
                break;
            }
        }
        dataMap.clear();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("cache initializing");
        init();
    }
}
