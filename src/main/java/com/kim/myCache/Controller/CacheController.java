package com.kim.myCache.Controller;

import com.kim.myCache.Cache.Entry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CacheController{
    @GetMapping(value = "/cache/{key}")
    public String Get(@PathVariable("key") String key){
        return "";
    }

    @PutMapping(value = "/cache")
    public String Store(@RequestBody List<Entry> entryList) {
        return "ok";
    }
}
