package org.example.myprojectbackend.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FlowUtils {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    public boolean limitOnceCheck(String key,int blockTime){
    if(stringRedisTemplate.hasKey(key)){
        return false;
    }else {
        stringRedisTemplate.opsForValue().set(key,"",blockTime, TimeUnit.SECONDS);
        return true;
    }
    }
}
