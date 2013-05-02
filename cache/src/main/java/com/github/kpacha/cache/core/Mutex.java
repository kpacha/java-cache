package com.github.kpacha.cache.core;

/**
 * Simple mutex interface
 * 
 * @author kpacha666@gmail.com
 */
public interface Mutex {
    public static final String MUTEX_KEY_PREFIX = "LOCK";

    /**
     * Try to lock the key.
     * 
     * @return true if the key was not locked and now it is, else false
     */
    boolean tryLock();

    /**
     * Check if the key is locked
     * 
     * @return
     */
    boolean stillHasLock();

    /**
     * Unlock the key
     * 
     * @return
     */
    boolean unlock();
}
