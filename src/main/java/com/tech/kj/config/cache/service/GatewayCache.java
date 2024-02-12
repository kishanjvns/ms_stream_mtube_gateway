package com.tech.kj.config.cache.service;

public interface GatewayCache {
    void put(String key,String value, long ttl);
    void put(String key,String value);
    Object getByKeyWithTTl(String key);

}
