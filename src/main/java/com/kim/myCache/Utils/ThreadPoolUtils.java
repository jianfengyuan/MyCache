package com.kim.myCache.Utils;


import java.util.concurrent.*;

/**
 * @program: MyCache
 * @description:
 * @author: Kim_yuan
 * @create: 2021-07-24 12:44
 **/

public class ThreadPoolUtils {
    public static ScheduledExecutorService createScheduledThread(){
        return new ScheduledThreadPoolExecutor(1);
    }

    public static ExecutorService createExecutor(){
        return new ThreadPoolExecutor(5, 100, 60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>());
    }
}
