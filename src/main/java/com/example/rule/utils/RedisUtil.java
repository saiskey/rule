package com.example.rule.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redicache 工具类
 */
@Component
@Slf4j
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     *
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object result = null;
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        if (result == null) {
            return null;
        }
        return result;
    }

    public Object getNew(final String key) {
        Object result = null;
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(Object.class));
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        if (result == null) {
            return null;
        }
        return result;
    }


    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            //redisTemplate.opsForValue().set(key,value);
            redisTemplate.expire(key, expireTime, TimeUnit.MILLISECONDS);
            result = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    public boolean hmset(String key, Map<String, String> value) {
        boolean result = false;
        try {
            redisTemplate.opsForHash().putAll(key, value);
            result = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    public Map<String, String> hmget(String key) {
        Map<String, String> result = null;
        try {
            result = redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }


    /**
     * 设置hash的值
     *
     * @param hKey  大key
     * @param key   小key
     * @param value 值
     * @return
     */
    public void setHashValue(String hKey, String key, Object value) {
        redisTemplate.opsForHash().put(hKey, key, value);
    }


    /**
     * 获取hash值
     *
     * @param hKey   大key
     * @param key    小key
     * @param tClass 泛型
     * @return
     */
    public <T> T getHashValue(String hKey, String key, Class<T> tClass) {
        Object o = redisTemplate.opsForHash().get(hKey, key);
        if (o == null) {
            return null;
        }
        return JSON.parseObject(o + "", tClass);
    }

}
