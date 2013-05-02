package com.github.kpacha.cache.core;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * The generic cache layer manager.
 * 
 * It encapsulates all the caching-related configuration and logic and decouples
 * the DAO layer from the cache driver.
 * 
 * @param <T>
 * @author kpacha666@gmail.com
 */
public abstract class CacheLayer<T> {

    public static final String ALL_KEY = "all";

    protected Set<String> aggregatedKeys = new HashSet<String>(1);

    /**
     * The cache layer default parameters
     */
    protected String keyPrefix = "Generic";
    protected int ttl = 3600;

    public CacheLayer() {
	aggregatedKeys.add(ALL_KEY);
    }

    protected void deleteAggregated() {
	for (String aggregatedKey : aggregatedKeys) {
	    delete(aggregatedKey);
	}
    }

    /**
     * Setter for the prefix to add to the received keys
     * 
     * @param keyPrefix
     */
    public void setKeyPrefix(String keyPrefix) {
	this.keyPrefix = keyPrefix;
    }

    /**
     * Setter for the default ttl
     * 
     * @param ttl
     */
    public void setTtl(int ttl) {
	this.ttl = ttl;
    }

    /**
     * Simple cache getter by key
     * 
     * @param key
     * @return The value or null
     */
    public abstract T get(String key);

    /**
     * Simple cache getter in order to get the complete collection of items
     * 
     * @return The value or null
     */
    public abstract Map<String, T> getAll();

    /**
     * Simple cache getter in order to get a filtered collection of items
     * 
     * @return The value or null
     */
    public abstract List<T> findBy(String signature);

    /**
     * Simple management of the wait and check strategy for lockings
     * 
     * @param key
     * @return The value or null
     */
    public abstract T manageLocking(String key);

    /**
     * Asynchronous getter
     * 
     * @param key
     * @param timeout
     * @return
     */
    public abstract T asyncGet(String key, long timeout);

    /**
     * The asyncGet method with a default timeout
     * 
     * @param key
     * @return
     */
    public abstract T asyncGet(String key);

    /**
     * The setter with default ttl
     * 
     * @param key
     * @param value
     * @return
     */
    public abstract Future<Boolean> set(String key, Object value);

    /**
     * Setter (adds and updates) by key, value and ttl.
     * 
     * @param key
     * @param ttl
     * @param value
     * @return
     */
    public abstract Future<Boolean> set(String key, int ttl, Object value);

    /**
     * Store the received object indexed as all with the default ttl
     * 
     * @param value
     * @return
     */
    public abstract Future<Boolean> setAll(Object value);

    /**
     * Delete the related collection
     * 
     * @return
     */
    public abstract Future<Boolean> deleteAll();

    /**
     * Delete the key from the cache
     * 
     * @param key
     * @return
     */
    public abstract Future<Boolean> delete(String key);

    /**
     * Checks if the cache key is locked
     * 
     * @param key
     * @return
     */
    public abstract boolean isLocked(String key);

    /**
     * Tries to lock the cache key
     * 
     * @param key
     * @return
     */
    public abstract boolean lock(String key);

    /**
     * Tries to unlock the cache key.
     * 
     * @param key
     * @return
     */
    public abstract boolean unlock(String key);

}
