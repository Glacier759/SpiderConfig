package com.glacier.spider.crawler.scheduler;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by glacier on 14-12-13.
 */
public class RedisScheduler {
    private Jedis redis;
    private JedisPool pool;
    private String key;

    public RedisScheduler(String key) {
        this.key = key;
        this.pool = new JedisPool(new JedisPoolConfig(), "localhost", 6379, 5000);
    }

    public void put(String value) {
        this.redis = pool.getResource();
        try {
            redis.lpush(key, value);
        }finally {
            pool.returnResource(redis);
        }
    }

    public String get() {
        String value = null;
        this.redis = pool.getResource();
        try {
            value = redis.rpop(key);
        }finally {
            pool.returnResource(redis);
        }
        return value;
    }

    public long length() {
        this.redis = pool.getResource();
        long length = redis.llen(key);
        pool.returnResource(redis);
        return length;
    }

    public void remove( String value ) {
        this.redis = pool.getResource();
        redis.lrem(key, 0, value);
        pool.returnResource(redis);
    }

    public void destory() {
        this.redis.flushAll();
        pool.destroy();
    }
}
