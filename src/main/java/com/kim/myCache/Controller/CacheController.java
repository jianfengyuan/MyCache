package com.kim.myCache.Controller;

import com.kim.myCache.Cache.Cache;
import com.kim.myCache.Entities.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class CacheController{
    @Resource
    private Cache<String,String> cache;
    @GetMapping(value = "/cache/{key}")
    public String Get(@PathVariable("key") String key){

        return cache.get(key);
    }

    @PostMapping(value = "/cache")
    public String Store(@RequestBody List<Entry<String,String>> entryList) {
        for (Entry e :
                entryList) {
            cache.add(e);
        }
        return "ok";
    }
}
