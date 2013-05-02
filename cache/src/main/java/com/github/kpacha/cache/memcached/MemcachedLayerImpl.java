package com.github.kpacha.cache.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.OperationTimeoutException;

import com.github.kpacha.cache.core.CacheLayer;

/**
 * The generic cache layer manager.
 * 
 * It encapsulates all the caching-related configuration and logic and decouples
 * the DAO layer from the memcached driver.
 * 
 * @param <T>
 * @see CacheLayer
 * @author kpacha666@gmail.com
 */
public class MemcachedLayerImpl<T> extends CacheLayer<T> {

    /**
     * Default memcached server configuration
     */
    protected static final String MEMCACHED_IP = "127.0.0.1";
    protected static final int MEMCACHED_PORT = 11211;

    /**
     * Default memcached server pool
     */
    public static List<InetSocketAddress> memcachedPool = Arrays
	    .asList(new InetSocketAddress(MEMCACHED_IP, MEMCACHED_PORT));

    /**
     * The memcached client to use
     */
    protected static ExtendedMemcachedClient memcached = null;

    /**
     * The cache layer constants
     */
    private static final String KEY_SEPARATOR = ExtendedMemcachedClient.KEY_SEPARATOR;
    private static final String WILDCARD = ExtendedMemcachedClient.WILDCARD;
    private static final long GET_TIMEOUT = 5;
    private static final int MAX_RETRIES = 10;

    /**
     * Creates a memcached layer setting-up the memcached client against the
     * default memcached pool
     * 
     * @throws IOException
     */
    public MemcachedLayerImpl() throws IOException {
	this(memcachedPool);
    }

    /**
     * Creates a memcached layer setting-up the memcached client against the
     * received memcached pool
     * 
     * @param memcachedPool
     * @throws IOException
     */
    public MemcachedLayerImpl(final List<InetSocketAddress> memcachedPool)
	    throws IOException {
	// TODO check if using a set of clients is an improvement
	if (memcached == null) {
	    memcached = new ExtendedMemcachedClientImpl(memcachedPool);
	}
    }

    /**
     * Simple memcached get wrapper
     * 
     * @param key
     * @return The value or null
     */
    @SuppressWarnings("unchecked")
    public T get(final String key) {
	T result = null;
	try {
	    result = (T) memcached.get(this.normalizeKey(key));
	} catch (OperationTimeoutException e) {
	    System.out.println(this.getClass().getSimpleName() + ".get(" + key
		    + ") exception: " + e);
	} catch (RuntimeException e) {
	    System.out.println(this.getClass().getSimpleName() + ".get(" + key
		    + ") exception: " + e);
	}
	return result;
    }

    public List<T> findBy(final String signature) {
	List<T> result = null;
	try {
	    result = (List<T>) memcached.get(this.normalizeKey(signature));
	} catch (OperationTimeoutException e) {
	    System.out.println(this.getClass().getSimpleName() + ".get("
		    + signature + ") exception: " + e);
	} catch (RuntimeException e) {
	    System.out.println(this.getClass().getSimpleName() + ".get("
		    + signature + ") exception: " + e);
	}
	return result;
    }

    /**
     * Simple memcached get wrapper in order to get the complete collection of
     * items
     * 
     * @return The value or null
     */
    @SuppressWarnings("unchecked")
    public Map<String, T> getAll() {
	Map<String, T> result = null;
	try {
	    result = (Map<String, T>) memcached.get(this.normalizeKey(ALL_KEY));
	} catch (OperationTimeoutException e) {
	    System.out.println(this.getClass().getSimpleName() + ".get("
		    + ALL_KEY + ") exception: " + e);
	} catch (RuntimeException e) {
	    System.out.println(this.getClass().getSimpleName() + ".get("
		    + ALL_KEY + ") exception: " + e);
	}
	return result;
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
	// if (result == null && !this.isLocked(key)) {
	// System.out.println("The key " + key
	// + " was not found and still locked");
	// }
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
	final Future<Object> future = memcached
		.asyncGet(this.normalizeKey(key));
	T result = null;
	try {
	    result = (T) future.get(timeout, TimeUnit.SECONDS);
	} catch (TimeoutException e) {
	    future.cancel(false);
	} catch (InterruptedException e) {
	    future.cancel(false);
	} catch (ExecutionException e) {
	    future.cancel(false);
	}
	return result;
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
     * Simple memcached set wrapper
     * 
     * @param key
     * @param value
     * @return
     */
    public Future<Boolean> set(final String key, final Object value) {
	this.delete(ALL_KEY);
	return memcached.set(this.normalizeKey(key), ttl, value);
    }

    /**
     * Simple memcached set wrapper
     * 
     * @param key
     * @param ttl
     * @param value
     * @return
     */
    public Future<Boolean> set(final String key, final int ttl,
	    final Object value) {
	this.delete(ALL_KEY);
	this.unlock(key);
	return memcached.set(this.normalizeKey(key), ttl, value);
    }

    /**
     * Store the received object indexed as all
     * 
     * @param value
     * @return
     */
    public Future<Boolean> setAll(final Object value) {
	return memcached.set(this.normalizeKey(ALL_KEY), ttl, value);
    }

    /**
     * Delete the related collection
     * 
     * @return
     */
    public Future<Boolean> deleteAll() {
	final Set<String> keys = memcached.selectKeys(this.normalizeKey("")
		+ WILDCARD);
	for (final String key : keys) {
	    this.delete(key);
	}
	deleteAggregated();
	return this.delete(ALL_KEY);
    }

    /**
     * Simple memcached delete wrapper
     * 
     * @param key
     * @return
     */
    public Future<Boolean> delete(final String key) {
	if (!key.equalsIgnoreCase(ALL_KEY)) {
	    this.delete(ALL_KEY);
	}
	return memcached.delete(this.normalizeKey(key));
    }

    /**
     * Simple memcached isLocked wrapper
     * 
     * @param key
     * @return
     */
    public boolean isLocked(final String key) {
	return memcached.isLocked(this.normalizeKey(key));
    }

    /**
     * Simple memcached lock wrapper
     * 
     * @param key
     * @return
     */
    public boolean lock(final String key) {
	return memcached.lock(this.normalizeKey(key));
    }

    /**
     * Simple memcached unlock wrapper
     * 
     * @param key
     * @return
     */
    public boolean unlock(final String key) {
	return memcached.unlock(this.normalizeKey(key));
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
