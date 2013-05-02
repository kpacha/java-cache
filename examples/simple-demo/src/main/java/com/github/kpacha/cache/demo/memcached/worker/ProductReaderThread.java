package com.github.kpacha.cache.demo.memcached.worker;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.github.kpacha.cache.core.Dao;
import com.github.kpacha.cache.demo.model.Product;
import com.github.kpacha.cache.demo.worker.AbstractThread;


/**
 * Product reader worker
 * 
 * @author kpacha666@gmail.com
 */
public class ProductReaderThread extends AbstractThread {

    private Dao<Product> productMemcachedDao;
    private List<Product> products;

    public ProductReaderThread(final String name) {
	super(name);
	this.totalLoops = 2;
    }

    /**
     * @param productMemcachedDao
     *            the productMemcachedDao to set
     */
    public ProductReaderThread setProductMemcachedDao(
	    Dao<Product> productMemcachedDao) {
	this.productMemcachedDao = productMemcachedDao;
	return this;
    }

    /**
     * @param products
     *            the products to set
     */
    public ProductReaderThread setProducts(List<Product> products) {
	this.products = products;
	this.totalEntities = products.size();
	return this;
    }

    @Override
    protected void doLoop() {
	Collections.shuffle(products);
	for (Product product : products) {
	    long startTime = new Date().getTime();
	    Product receivedProduct = productMemcachedDao.get(product.getSku());
	    checkIterationDuration(product.getSku(), new Date().getTime()
		    - startTime);
	    checkIterationResult(product.getSku(), product, receivedProduct);
	}
    }

}
