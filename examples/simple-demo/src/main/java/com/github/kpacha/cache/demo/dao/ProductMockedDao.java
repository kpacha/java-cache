package com.github.kpacha.cache.demo.dao;

import com.github.kpacha.cache.core.Dao;
import com.github.kpacha.cache.demo.model.Product;

/**
 * Sample product dao with a local store
 * 
 * @see Dao
 * @see AbstractMockedDao
 * @author kpacha666@gmail.com
 */
public class ProductMockedDao extends AbstractMockedDao<Product> implements
	Dao<Product> {

    public boolean set(final Product product) {
	catalog.put(product.getSku(), product);
	return true;
    }

    public boolean set(final String sku, final Product product) {
	boolean result = false;
	if (sku == product.getSku()) {
	    catalog.put(sku, product);
	    result = true;
	}
	return result;
    }

    public boolean delete(final Product product) {
	return this.delete(product.getSku());
    }
}
