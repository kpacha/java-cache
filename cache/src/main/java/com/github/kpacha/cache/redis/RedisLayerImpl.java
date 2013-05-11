package com.github.kpacha.cache.redis;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.github.kpacha.cache.core.CacheLayer;
import com.github.kpacha.cache.core.JsonMapper;
import com.github.kpacha.cache.core.Mutex;
import com.github.kpacha.cache.core.mapper.JacksonMapper;

/**
 * The generic cache layer manager.
 * 
 * It encapsulates all the caching-related configuration and logic and decouples
 * the DAO layer from the redis driver.
 * 
 * @param <T>
 * @see CacheLayer
 * @author kpacha666@gmail.com
 */
public class RedisLayerImpl<T> extends CacheLayer<T> {

    /**
     * Default memcached server configuration
     */
    protected static final String REDIS_IP = "127.0.0.1";
    protected static final int REDIS_PORT = 6379;

    /**
     * Default redis server pool
     */
    public static List<InetSocketAddress> redisPool = Arrays
	    .asList(new InetSocketAddress(REDIS_IP, REDIS_PORT));

    /**
     * The redis client pool
     */
    protected static JedisPool jedisPool = null;

    /**
     * The cache layer constants
     */
    private static final String KEY_SEPARATOR = ":";
    private static final String WILDCARD = "*";
    private static final long GET_TIMEOUT = 5;
    private static final int MAX_RETRIES = 10;

    /**
     * The json mapper
     */
    private JsonMapper mapper = null;

    /**
     * Creates a redis layer setting-up the redis client pool against the
     * default redis server pool
     * 
     * @param clazz
     * @throws IOException
     */
    public RedisLayerImpl(final Class<?> clazz) throws IOException {
	this(redisPool, clazz);
    }

    /**
     * Creates a redis layer setting-up the redis client pool against the
     * default redis server pool
     * 
     * @param redisPool
     * @param clazz
     * @throws IOException
     */
    public RedisLayerImpl(final List<InetSocketAddress> redisPool,
	    final Class<?> clazz) throws IOException {
	this(redisPool, getDefaultJsonMapper(clazz));
    }

    /**
     * Creates a redis layer setting-up the redis client against the received
     * redis server pool
     * 
     * @param redisPool
     * @param mapper
     * @throws IOException
     */
    public RedisLayerImpl(final List<InetSocketAddress> redisPool,
	    final JsonMapper mapper) throws IOException {
	super();
	initRedisPool();
	this.mapper = mapper;
    }

    /**
     * Simple redis get wrapper
     * 
     * @param key
     * @return The value or null
     */
    @SuppressWarnings("unchecked")
    public T get(final String key) {
	return (T) doGet(key);
    }

    @SuppressWarnings("unchecked")
    public List<T> findBy(final String signature) {
	return (List<T>) doGet(signature);
    }

    /**
     * Simple redis get wrapper in order to get the complete collection of items
     * 
     * @return The value or null
     */
    @SuppressWarnings("unchecked")
    public Map<String, T> getAll() {
	return (Map<String, T>) doGet(ALL_KEY);
    }

    /**
     * Simple management of the wait and check strategy for lockings
     * 
     * @param key
     * @return The value or null
     */
    public T manageLocking(final String key) {
	T result = null;
	for (int i = 0; i < MAX_RETRIES && this.isLocked(key) && result == null; i++) {
	    synchronized (this) {
		try {
		    this.wait(100);
		} catch (InterruptedException e) {
		    System.out.println(this.getClass().getSimpleName()
			    + ".manageLocking(" + key + ") exception: " + e);
		}
	    }
	    result = this.asyncGet(key);
	}
	return result;
    }

    /**
     * Simple wrapper for the asyncGet method
     * 
     * @param key
     * @param timeout
     * @return
     */
    @SuppressWarnings("unchecked")
    public T asyncGet(final String key, final long timeout) {
	// TODO
	return null;
    }

    /**
     * Simple wrapper for the asyncGet method with a default timeout
     * 
     * @param key
     * @return
     */
    public T asyncGet(final String key) {
	return (T) this.asyncGet(key, GET_TIMEOUT);
    }

    /**
     * Simple redis set wrapper
     * 
     * @param key
     * @param value
     */
    public void set(final String key, final Object value) {
	this.delete(ALL_KEY);
	doSet(key, ttl, value);
    }

    /**
     * Simple redis set wrapper
     * 
     * @param key
     * @param ttl
     * @param value
     */
    public void set(final String key, final int ttl, final Object value) {
	this.delete(ALL_KEY);
	this.unlock(key);
	doSet(key, ttl, value);
    }

    /**
     * Store the received object indexed as all
     * 
     * @param value
     */
    public void setAll(final Object value) {
	doSet(ALL_KEY, ttl, value);
    }

    /**
     * Delete the related collection
     */
    public void deleteAll() {
	final Set<String> keys = jedis().keys(this.normalizeKey("") + WILDCARD);
	for (final String key : keys) {
	    doDelete(key);
	}
	deleteAggregated();
    }

    /**
     * Simple redis delete wrapper
     * 
     * @param key
     */
    public void delete(final String key) {
	if (!key.equalsIgnoreCase(ALL_KEY)) {
	    doDelete(ALL_KEY);
	}
	doDelete(key);
    }

    /**
     * Simple redis isLocked wrapper
     * 
     * @param key
     * @return
     */
    public boolean isLocked(final String key) {
	return getMutex(key).stillHasLock();
    }

    /**
     * Simple redis lock wrapper
     * 
     * @param key
     * @return
     */
    public boolean lock(final String key) {
	return getMutex(key).tryLock();
    }

    /**
     * Simple redis unlock wrapper
     * 
     * @param key
     * @return
     */
    public boolean unlock(final String key) {
	return getMutex(key).unlock();
    }

    /* HELPER FUNCTIONS */

    private static final int LOCKING_TIMEOUT = 300;

    private Mutex getMutex(final String key) {
	return new RedisMutexImpl(jedis(), key, LOCKING_TIMEOUT);
    }

    protected Boolean doSet(final String key, final int ttl, final Object value) {
	final String normalizedKey = normalizeKey(key);
	jedis().set(normalizedKey, mapper.serialize(value));
	jedis().expire(normalizedKey, ttl);
	// TODO check the result code before sending the ok
	return true;
    }

    protected Boolean doDelete(final String key) {
	return jedis().del(this.normalizeKey(key)) > 0;
    }

    protected Object doGet(final String key) {
	return mapper.unserialize(jedis().get(this.normalizeKey(key)));
    }

    private synchronized void initRedisPool() {
	if (jedisPool == null) {
	    jedisPool = buildJedisPool(redisPool);
	}
    }

    private static JsonMapper getDefaultJsonMapper(final Class<?> clazz) {
	return new JacksonMapper(clazz);
    }

    private JedisPool buildJedisPool(final List<InetSocketAddress> redisPool) {
	final InetSocketAddress master = redisPool.get(0);
	return new JedisPool(master.getHostString(), master.getPort());
    }

    private Jedis jedis() {
	return jedisPool.getResource();
    }

    /**
     * Normalize the key with the keyPrefix
     * 
     * @param key
     * @return
     */
    protected String normalizeKey(final String key) {
	return keyPrefix + KEY_SEPARATOR + key;
    }

}
