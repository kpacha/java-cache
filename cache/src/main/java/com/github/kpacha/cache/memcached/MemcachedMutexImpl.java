package com.github.kpacha.cache.memcached;

import net.spy.memcached.CASValue;
import net.spy.memcached.OperationTimeoutException;

import com.github.kpacha.cache.core.Mutex;

/**
 * Mutex implementation using the memcached driver
 * 
 * @see Mutex
 * @see ExtendedMemcachedClient
 * @author kpacha666@gmail.com
 */
public class MemcachedMutexImpl implements Mutex {
    private ExtendedMemcachedClient memcached;
    private String key;
    private int maxTimeout;

    public MemcachedMutexImpl(final ExtendedMemcachedClient memcached,
	    final String key, final int maxTimeout) {
	this.memcached = memcached;
	this.key = key;
	this.maxTimeout = maxTimeout;
    }

    public boolean tryLock() {
	boolean lockedSucces = false;
	final String lockingKey = this.getLockingKey(key);
	try {
	    final CASValue<Object> locked = memcached.gets(lockingKey);
	    if (locked == null) {
		memcached.add(lockingKey, maxTimeout, true);
		lockedSucces = true;
	    } else if (!(Boolean) locked.getValue()) {
		memcached.cas(lockingKey, locked.getCas(), true);
		lockedSucces = true;
	    }
	} catch (OperationTimeoutException e) {
	    System.out.println(this.getClass().getSimpleName() + ".tryLock("
		    + key + ") exception: " + e);
	} catch (RuntimeException e) {
	    System.out.println(this.getClass().getSimpleName() + ".tryLock("
		    + key + ") exception: " + e);
	}
	return lockedSucces;
    }

    public boolean stillHasLock() {
	Boolean isMemcachedLocked = this.isLocked();
	if (isMemcachedLocked == null) {
	    isMemcachedLocked = false;
	}
	return isMemcachedLocked;
    }

    public boolean unlock() {
	memcached.delete(this.getLockingKey(key));
	return true;
    }

    /**
     * Normalize the locking key
     * 
     * @param key
     * @return
     */
    private String getLockingKey(final String key) {
	return MUTEX_KEY_PREFIX + ExtendedMemcachedClient.KEY_SEPARATOR + key;
    }

    private Boolean isLocked() {
	Boolean result = null;
	try {
	    result = (Boolean) memcached.get(this.getLockingKey(key));
	} catch (OperationTimeoutException e) {
	    System.out.println(this.getClass().getSimpleName() + " - "
		    + memcached.getClass().getSimpleName() + ".get("
		    + this.getLockingKey(key) + ") exception: " + e);
	} catch (RuntimeException e) {
	    System.out.println(this.getClass().getSimpleName() + " - "
		    + memcached.getClass().getSimpleName() + ".get("
		    + this.getLockingKey(key) + ") exception: " + e);
	}
	return result;
    }
}
