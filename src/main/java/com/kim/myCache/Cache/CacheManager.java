package com.kim.myCache.Cache;

import com.kim.myCache.Entities.Entry;
import com.kim.myCache.Utils.FileWriterReader;
import com.kim.myCache.Utils.ThreadPoolUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import com.kim.myCache.Cache.EventExecutor.Event;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/***
 * @Author kim_yuan
 * @Description main implementation for simple cache
 * @Date 9:36 上午 26/7/21
 * @return
 **/
public class CacheManager<K,V> implements Cache<K,V>, InitializingBean, DisposableBean {
    private static final long MAX_ACTIVE_EXPIRE_CYCLE_LOOKUPS_PER_LOOP = 1<<8;
    private long countThreadsHold=50;
    private Map<K, Entry<K,V>> dataMap;
    private Map<K, Long> timeMap;
    private ScheduledExecutorService scheduler;
    private FileWriterReader wr;
    private EventExecutor eventExecutor;
    private String cacheRoot;

    /***
     * @Author kim_yuan
     * @Description CacheManager initialization
     * @Date 9:37 上午 26/7/21
     * @param
     * @return void
     **/
    private void init() {
        try {
            String path = ResourceUtils.getURL("classpath:").getPath() +File.separator+ "cache";
            File file = new File(path);
            if (! file.exists()) {
                file.mkdir();
            }
            cacheRoot = path;
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        dataMap = new LRUConcurrentCache<>(64,1<<16);
        timeMap = new ConcurrentHashMap<>();
        wr = new FileWriterReader(cacheRoot);
        eventExecutor = new EventExecutor(wr);
        initExpireScheduler();
    }

    /***
     * @Author kim_yuan
     * @Description ExpireScheduler initialization
     * @Date 9:40 上午 26/7/21
     * @param
     * @return void
     **/
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
    /***
     * @Author kim_yuan
     * @Description To check and remove expire caches from LRU cache and disk.
     * @Date 9:42 上午 26/7/21
     * @param
     * @return java.lang.Boolean
     **/
    private Boolean expireCheckAndRemove() {
        int timeSize = timeMap.size();
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        Cache.logger.info("current cache size: " + timeSize);
        Cache.logger.info("current countThreadsHold: " + countThreadsHold);
        if (timeSize ==0) {
            return false;
        }
        int count = 0;
        int expire_count = 0;
        List<String> expireList = new ArrayList<>();
        Object[] keys = timeMap.keySet().toArray();
        while (count < countThreadsHold && count < timeSize){
            count++;
            int i = random.nextInt(timeSize);
            String key = keys[i].toString();
            Long time = timeMap.get(key);
            if (time < System.currentTimeMillis()) {
                expireList.add(key);
                expire_count++;
            }
        }
        Cache.logger.info(expire_count + " entries expired");
        for (String k :
                expireList) {
            dataMap.remove(k);
            timeMap.remove(k);
            eventExecutor.addEvent(Event.REMOVE,new Entry<>(k,null));
        }
        return expire_count > countThreadsHold / 4;
    }

    @Override
    public Boolean add(Entry<K, V> e) {
        if (e.getExpireTime() == null) {
            e.setExpireTime(10L);
            e.setUnit(TimeUnit.SECONDS);
        }
        dataMap.put(e.getKey(), e);
        timeMap.put(e.getKey(), e.getCreateTime() + e.getUnit().toMillis(e.getExpireTime()));
        eventExecutor.addEvent(Event.PUT, e);
        return true;
    }

    @Override
    public V get(K key) {
        Entry<K, V> e;
        e = dataMap.get(key);
        if (e.isExpire()) {
            dataMap.remove(key);
            eventExecutor.addEvent(Event.REMOVE,e);
            return null;
        }
        e = wr.read(key);
        if (e == null) {
            return null;
        }
        if (e.isExpire()) {
            wr.remove(key);
            return null;
        }
        dataMap.put(key, e);
        return e.getValue();
    }

    @Override
    public void destroy(){
        logger.info("cache cleaning");
        scheduler.shutdown();
        while (true) {
            if (scheduler.isTerminated()) {
                break;
            }
        }
        dataMap.clear();
        eventExecutor.destroy();
        wr.clear();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("cache initializing");
        init();
    }
}
