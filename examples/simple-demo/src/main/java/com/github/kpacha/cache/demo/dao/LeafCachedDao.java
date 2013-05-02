package com.github.kpacha.cache.demo.dao;

import java.io.IOException;

import com.github.kpacha.cache.core.AbstractCachedDao;
import com.github.kpacha.cache.core.CacheLayer;
import com.github.kpacha.cache.core.Dao;
import com.github.kpacha.cache.demo.model.Leaf;

/**
 * Simple leaf DAO proxy with a cache layer
 * 
 * @see Dao
 * @see AbstractCachedDao
 * @author kpacha666@gmail.com
 */
public class LeafCachedDao extends AbstractCachedDao<Leaf> implements
	Dao<Leaf> {

    /** the prefix to use */
    private final static String KEY_PREFIX = "Leaf";

    /** the ttl for the related keys */
    private final static int TTL = 60;

    public LeafCachedDao(final CacheLayer<Leaf> cache,
	    final Dao<Leaf> injectedDao) throws IOException {
	super(cache, injectedDao, KEY_PREFIX, TTL);
	lockingStrategy = LOCKING_OFF;
    }

    public boolean set(final Leaf leaf) {
	return this.set(leaf.getEmail(), leaf);
    }

    public boolean delete(final Leaf leaf) {
	return this.delete(leaf.getEmail());
    }

}
