package com.kim.myCache.Cache;

import com.kim.myCache.Entities.Entry;
import com.kim.myCache.Utils.FileWriterReader;
import com.kim.myCache.Utils.ThreadPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * @program: MyCache
 * @description:
 * @author: Kim_yuan
 * @create: 2021-07-25 16:28
 **/

public class EventExecutor {
    private ExecutorService createExecutor;
    private static FileWriterReader wr;

    public EventExecutor(FileWriterReader fileWriterReader) {
        wr = fileWriterReader;
        initExecutor();
    }

    private void initExecutor() {
        createExecutor = ThreadPoolUtils.createExecutor();
    }

    public <K,V>void addEvent(Event event,Entry<K,V> entry) {
        createExecutor.execute(new Runnable() {
            @Override
            public void run() {
                event.handle(entry);
            }
        });
    }

    /***
     * @Author kim_yuan
     * @Description Enmu class for Event
     * @Date 9:53 上午 26/7/21
     * @param
     * @return
     **/
    public enum Event{
        PUT{
            @Override
            <K, V> void handle(Entry<K, V> e) {
                logger.info("PUT is running...");
                wr.add(e.getKey(), e);
            }
        },
        REMOVE{
            @Override
            <K, V> void handle(Entry<K, V> e) {
                logger.info("REMOVE is running...");
                Boolean remove = wr.remove(e.getKey());
                logger.info("remove success: " + remove);
            }
        };
        Logger logger = LoggerFactory.getLogger(Event.class);
        abstract <K,V>void handle(Entry<K,V> e);
    }

    public void destroy() {
        createExecutor.shutdown();
        while (true) {
            if (createExecutor.isTerminated()) {
                break;
            }
        }
    }
}
