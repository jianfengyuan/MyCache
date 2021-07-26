package com.kim.myCache.Config;

import com.kim.myCache.Cache.Cache;
import com.kim.myCache.Cache.CacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: MyCache
 * @description:
 * @author: Kim_yuan
 * @create: 2021-07-24 16:22
 **/

@Configuration
public class CacheConfig {
    @Bean
    public Cache<String,String> createCacheManager() {
        return new CacheManager<>();
    }
}
