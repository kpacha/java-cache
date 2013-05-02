package com.github.kpacha.cache.demo.dao;

import java.io.IOException;

import com.github.kpacha.cache.core.AbstractCachedDao;
import com.github.kpacha.cache.core.CacheLayer;
import com.github.kpacha.cache.core.Dao;
import com.github.kpacha.cache.demo.model.Product;

/**
 * Proxy for the product dao with a cache layer.
 * 
 * @see Dao
 * @see AbstractCachedDao
 * @author kpacha666@gmail.com
 */
public class ProductCachedDao extends AbstractCachedDao<Product> implements
	Dao<Product> {

    /** the prefix to add to the key */
    private final static String KEY_PREFIX = "Product";

    /**
     * Creates a new cached-enhanced product dao based on the received regular
     * one. This constructor does not update the default cache layer ttl
     * 
     * @param cache
     * @param injectedDao
     * @throws IOException
     */
    public ProductCachedDao(final CacheLayer<Product> cache,
	    final Dao<Product> injectedDao) throws IOException {
	super(cache, injectedDao, KEY_PREFIX);
	lockingStrategy = LOCKING_OFF;
    }

    /**
     * Add or update a product
     * 
     * @param product
     * @return
     */
    public boolean set(final Product product) {
	return this.set(product.getSku(), product);
    }

    /**
     * Remove a product from the store and cache
     * 
     * @param product
     * @return
     */
    public boolean delete(final Product product) {
	return this.delete(product.getSku());
    }

}
