package com.github.kpacha.cache.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.OperationTimeoutException;

import com.github.kpacha.cache.core.Mutex;

/**
 * Extended memcached client implementation.
 * 
 * Delegates in MemcachedMutex the lock and unlock management.
 * 
 * @see ExtendedMemcachedIF
 * @see Mutex
 * @author kpacha666@gmail.com
 */
public class ExtendedMemcachedClientImpl extends MemcachedClient implements
	ExtendedMemcachedClient {

    public ExtendedMemcachedClientImpl(InetSocketAddress... ia)
	    throws IOException {
	super(ia);
    }

    public ExtendedMemcachedClientImpl(List<InetSocketAddress> addrs)
	    throws IOException {
	super(addrs);
    }

    public ExtendedMemcachedClientImpl(ConnectionFactory cf,
	    List<InetSocketAddress> addrs) throws IOException {
	super(cf, addrs);
    }

    /**
     * Get a sorted set of the stored keys filtered with a regex expression
     * 
     * @see java.util.regex.Pattern
     * 
     * @param regex
     * @return A sorted set of stored keys
     * @throws IllegalStateException
     * @throws OperationTimeoutException
     */
    public Set<String> selectKeys(final String regex)
	    throws IllegalStateException, OperationTimeoutException {
	// clean and compile the regex pattern
	String cleanRegex = WILDCARD;
	if (regex != null) {
	    cleanRegex = regex;
	}
	final Pattern selector = Pattern.compile(cleanRegex);
	final Set<String> selectedKeys = new HashSet<String>();
	for (final Entry<Integer, Integer> totalItemsBySlab : this
		.countItemsBySlabId().entrySet()) {
	    for (final String key : this.getKeysBySlab(
		    totalItemsBySlab.getKey(), totalItemsBySlab.getValue())) {
		if (selector.matcher(key).matches()) {
		    selectedKeys.add(key);
		}
	    }
	}
	return new TreeSet<String>(selectedKeys);
    }

    /**
     * Get a sorted set of all the stored keys
     * 
     * @return A sorted set of stored keys
     * @throws IllegalStateException
     * @throws OperationTimeoutException
     */
    public Set<String> getKeys() throws IllegalStateException,
	    OperationTimeoutException {
	return this.selectKeys(WILDCARD);
    }

    /**
     * Lock the received key
     * 
     * @param key
     * @return
     */
    public Boolean lock(final String key) {
	return this.getMutex(key).tryLock();
    }

    /**
     * Unlock the received key
     * 
     * @param key
     * @return
     */
    public Boolean unlock(final String key) {
	return this.getMutex(key).unlock();
    }

    /**
     * Check if the key is locked
     * 
     * @param key
     * @return
     */
    public Boolean isLocked(final String key) {
	return this.getMutex(key).stillHasLock();
    }

    /* HELPER FUNCTIONS */

    private static final int LOCKING_TIMEOUT = 300;

    /**
     * Regex to determine if a string contains an integer
     * 
     * @see java.util.regex.Pattern
     */
    private static final Pattern IS_INTEGER = Pattern.compile("\\d{1,}");

    /**
     * Gets a MemcachedMutex with the received key and the default parameters
     * 
     * @param key
     * @return
     */
    private Mutex getMutex(final String key) {
	Mutex result;
	result = new MemcachedMutexImpl(this, key, LOCKING_TIMEOUT);
	return result;
    }

    /**
     * Get the set of stored keys by slab id
     * 
     * @param slabId
     *            The slab identifier
     * @param limit
     *            The limit of keys to return
     * @return
     * @throws IllegalStateException
     */
    private Set<String> getKeysBySlab(final Integer slabId, final Integer limit)
	    throws IllegalStateException {
	final Set<String> keys = new HashSet<String>();
	for (final Map<String, String> cacheEntries : this.getStats(
		"cachedump " + slabId + " " + limit).values()) {
	    keys.addAll(cacheEntries.keySet());
	}
	return keys;
    }

    /**
     * Get the set of slab identifiers from the connected Memcached servers with
     * the number of used chunks
     * 
     * @return
     * @throws IllegalStateException
     */
    private Map<Integer, Integer> countItemsBySlabId()
	    throws IllegalStateException {
	final Map<Integer, Integer> slabIds = new HashMap<Integer, Integer>();
	for (final Map<String, String> slabStatistics : this.getStats("slabs")
		.values()) {
	    for (final String key : slabStatistics.keySet()) {
		final String[] splitedKey = key.split(":");
		if (splitedKey.length == 2
			&& IS_INTEGER.matcher(splitedKey[0]).matches()
			&& splitedKey[1].equalsIgnoreCase("used_chunks")) {
		    slabIds.put(Integer.parseInt(splitedKey[0]),
			    Integer.parseInt(slabStatistics.get(key)));
		}
	    }
	}
	return slabIds;
    }
}
