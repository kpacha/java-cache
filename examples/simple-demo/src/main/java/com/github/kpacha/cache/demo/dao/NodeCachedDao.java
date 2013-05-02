package com.github.kpacha.cache.demo.dao;

import java.io.IOException;

import com.github.kpacha.cache.core.AbstractCachedDao;
import com.github.kpacha.cache.core.CacheLayer;
import com.github.kpacha.cache.core.Dao;
import com.github.kpacha.cache.demo.model.Node;

/**
 * Simple node DAO proxy with a cache layer
 * 
 * @see Dao
 * @see AbstractCachedDao
 * @author kpacha666@gmail.com
 */
public class NodeCachedDao extends AbstractCachedDao<Node> implements
	Dao<Node> {

    /** the prefix to use */
    private final static String KEY_PREFIX = "Node";

    /** the ttl for the related keys */
    private final static int TTL = 1800;

    public NodeCachedDao(final CacheLayer<Node> cache,
	    final Dao<Node> injectedDao) throws IOException {
	super(cache, injectedDao, KEY_PREFIX, TTL);
	lockingStrategy = LOCKING_OFF;
    }

    public boolean set(final Node node) {
	return this.set(node.getName(), node);
    }

    public boolean delete(final Node node) {
	return this.delete(node.getName());
    }

}
