package com.github.kpacha.cache.redis;

import redis.clients.jedis.Jedis;

import com.github.kpacha.cache.core.Mutex;

/**
 * Mutex implementation using the jedis driver
 * 
 * @see Mutex
 * @see Jedis
 * @author kpacha666@gmail.com
 */
public class RedisMutexImpl implements Mutex {
    private Jedis jedis;
    private String key;
    private int maxTimeout;

    private final static long LOCKED = 1l;

    public RedisMutexImpl(final Jedis jedis, final String key,
	    final int maxTimeout) {
	this.jedis = jedis;
	this.key = key;
	this.maxTimeout = maxTimeout;
    }

    public boolean tryLock() {
	return (jedis.sadd(MUTEX_KEY_PREFIX, key) == LOCKED);
    }

    public boolean stillHasLock() {
	return jedis.sismember(MUTEX_KEY_PREFIX, key);
    }

    public boolean unlock() {
	jedis.srem(MUTEX_KEY_PREFIX, key);
	return true;
    }
}
