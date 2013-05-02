package com.github.kpacha.cache.core;

import java.util.Map;

import junit.framework.Assert;

/**
 * Abstract and generic cache-enhanced dao implementation.
 * 
 * It acts like a dao proxy and implements the common methods of the dao
 * interface, managing the cache client and delegating to a protected dao
 * (usually jpa-based) all the tasks related to the persistent layer (db
 * interactions).
 * 
 * @param <T>
 * @see Dao
 * @author kpacha666@gmail.com
 */
abstract public class AbstractCachedDao<T> implements Dao<T> {

    protected static final int LOCKING_OFF = 0;
    protected static final int LOCKING_TOLERANT = 1;
    protected static final int LOCKING_ON = 2;

    /**
     * The traditional dao (usually jpa-based)
     */
    protected Dao<T> dao = null;

    /**
     * The cache manager
     */
    protected CacheLayer<T> cache = null;

    /**
     * The cache parameters to use with the layer.
     */
    protected String keyPrefix = "Generic";

    protected int lockingStrategy = LOCKING_ON;
    protected static int ttl = 3600;

    /**
     * Default constructor. It declares a cache layer and set up the received
     * prefix and ttl
     * 
     * @param cache
     * @param dao
     * @param keyPrefix
     * @param defaultTtl
     */
    public AbstractCachedDao(final CacheLayer<T> cache, final Dao<T> dao,
	    final String keyPrefix, final int defaultTtl) {
	init(cache, dao, keyPrefix, defaultTtl);
    }

    /**
     * Default constructor. It updates the cache layer and set up the received
     * prefix
     * 
     * @param cache
     * @param dao
     * @param keyPrefix
     */
    public AbstractCachedDao(final CacheLayer<T> cache, final Dao<T> dao,
	    final String keyPrefix) {
	init(cache, dao, keyPrefix, ttl);
    }

    /**
     * Init process: it updates the cache layer and set up the received prefix
     * and default ttl
     * 
     * @param cache
     * @param dao
     * @param keyPrefix
     * @param defaultTtl
     */
    void init(final CacheLayer<T> cache, final Dao<T> dao,
	    final String keyPrefix, final int defaultTtl) {
	// fail-fast check
	Assert.assertNotNull(cache);
	Assert.assertNotNull(dao);
	Assert.assertNotNull(keyPrefix);
	Assert.assertNotSame("", keyPrefix);

	this.dao = dao;
	ttl = defaultTtl;
	cache.setTtl(defaultTtl);
	this.keyPrefix = keyPrefix;
	cache.setKeyPrefix(keyPrefix);
	this.cache = cache;
    }

    /**
     * Remove an entity from the store and the cache by its id
     * 
     * @param entityId
     * @return
     */
    public boolean delete(final String entityId) {
	dao.delete(entityId);
	cache.delete(entityId);
	return true;
    }

    /**
     * Get an entity from the cache or the store by its id
     * 
     * @param entityId
     * @return
     */
    public T get(final String entityId) {
	T entity = cache.get(entityId);
	if (entity == null) {
	    switch (lockingStrategy) {
	    case LOCKING_OFF:
		entity = delegatedGet(entityId);
		break;
	    case LOCKING_TOLERANT:
		entity = getIfUnlocked(entityId);
		break;
	    case LOCKING_ON:
	    default:
		entity = getGentle(entityId);
		break;
	    }
	}
	return entity;
    }

    /**
     * Get an entity from the cache or the store by its id. It will manage the
     * locking strategy
     * 
     * @param entityId
     * @return
     */
    public T getGentle(final String entityId) {
	T entity = cache.manageLocking(entityId);
	if (entity == null) {
	    entity = getIfUnlocked(entityId);
	}
	return entity;
    }

    /**
     * Get an entity from the cache or the store by its id and do not wait if is
     * locked
     * 
     * @param entityId
     * @return the entity or null if it is unreachable and locked
     */
    public T getIfUnlocked(final String entityId) {
	T entity = null;
	if (!cache.isLocked(entityId)) {
	    cache.lock(entityId);
	    entity = delegatedGet(entityId);
	}
	return entity;
    }

    /**
     * Get an entity from the cache or the store by its id and do not care if
     * it's locked
     * 
     * @param entityId
     * @return the entity or null if it is unreachable and locked
     */
    public T getLockless(final String entityId) {
	return delegatedGet(entityId);
    }

    /**
     * Get all the stored entities from the cache or from the store. If the data
     * is retrieved from the dao, add the collection to the cache
     * 
     * @return
     */
    public Map<String, T> getAll() {
	Map<String, T> entities = cache.getAll();
	if (entities == null) {
	    entities = dao.getAll();
	    if (entities != null) {
		cache.setAll(entities);
	    }
	}
	return entities;
    }

    /**
     * Add or update an entity in the store and the cache
     * 
     * @param entity
     * @return
     */
    public boolean set(final String entityId, final T entity) {
	dao.set(entity);
	cache.set(entityId, entity);
	return true;
    }

    /**
     * Simple wrapper for the asyncGet method with a default timeout
     * 
     * @param key
     * @return
     */
    public T asyncGet(final String key) {
	return cache.asyncGet(key);
    }

    /**
     * Simple wrapper for the asyncGet method
     * 
     * @param key
     * @param timeout
     * @return
     */
    public T asyncGet(final String key, final long timeout) {
	return cache.asyncGet(key, timeout);
    }

    /**
     * Lock the key, delegate the search to the dao, store the entity in the
     * cache and unlock the key
     * 
     * @param entityId
     * @return
     */
    protected T delegatedGet(final String entityId) {
	T entity = dao.get(entityId);
	if (entity != null) {
	    cache.set(entityId, entity);
	} else {
	    System.out.println("The entity " + entityId
		    + " was not found in the mocked store!");
	}
	cache.unlock(entityId);
	return entity;
    }

}
