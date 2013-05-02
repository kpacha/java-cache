package com.github.kpacha.cache.memcached;

import java.util.Set;

import net.spy.memcached.MemcachedClientIF;
import net.spy.memcached.OperationTimeoutException;

/**
 * Interface for the extended memcached client. It adds some useful methods to
 * the memcached client in order to:
 * 
 * - manage the key locking and
 * 
 * - expose several methods to retrieve the stored key names
 * 
 * @author kpacha666@gmail.com
 */
public interface ExtendedMemcachedClient extends MemcachedClientIF {

    public static final String KEY_SEPARATOR = ":";

    /**
     * Regex wildcard in order to retrieve all the keys
     */
    public static final String WILDCARD = ".*";

    /**
     * Get a sorted set of the stored keys filtered with a regex expression
     * 
     * @see java.util.regex.Pattern
     * 
     * @param regex
     * @return A set of stored keys
     * @throws IllegalStateException
     * @throws OperationTimeoutException
     */
    Set<String> selectKeys(final String regex) throws IllegalStateException,
	    OperationTimeoutException;

    /**
     * Get a sorted set of all the stored keys
     * 
     * @return A set of stored keys
     * @throws IllegalStateException
     * @throws OperationTimeoutException
     */
    Set<String> getKeys() throws IllegalStateException,
	    OperationTimeoutException;

    /**
     * Lock the received key
     * 
     * @param key
     * @return
     * @throws IllegalStateException
     * @throws OperationTimeoutException
     */
    Boolean lock(final String key) throws IllegalStateException,
	    OperationTimeoutException;

    /**
     * Unlock the received key
     * 
     * @param key
     * @return
     * @throws IllegalStateException
     * @throws OperationTimeoutException
     */
    Boolean unlock(final String key) throws IllegalStateException,
	    OperationTimeoutException;

    /**
     * Check if the key is locked
     * 
     * @param key
     * @return
     */
    Boolean isLocked(final String key);
}
