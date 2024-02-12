package com.tech.kj.config.cache.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class RedisCacheService implements GatewayCache{
    private static final Logger log = LoggerFactory.getLogger(RedisCacheService.class);
    @Autowired
    private Jedis jedis;

    @Override

    public void put(String key, String value, long ttl) {
        log.info("put token into redis");
        jedis.setex(key,ttl,"default");
    }

    @Override
    public void put(String key, String value) {
        //jedis.ttl(key)
    }
    @Override
    public  Object getByKeyWithTTl(String key){
        log.info("get token from redis");
        Object object = jedis.ttl(key);
        log.info("retrieved {} from redis ",object);
        return object;
    }
}
