package com.github.kpacha.cache.demo;

import junit.framework.Assert;

import com.github.kpacha.cache.Config;
import com.github.kpacha.cache.core.Dao;
import com.github.kpacha.cache.demo.model.Product;

/**
 * Common method for testing all the product DAOs implementations
 * 
 * @author kpacha666@gmail.com
 */
abstract public class AbstractTestProductDao extends Config {

    protected static Dao<Product> dao = null;
    protected static int totalProducts = 10;

    protected final void doTestGetAll() {
	Assert.assertEquals(totalProducts, dao.getAll().size());
    }

    protected final void doTestGet() {
	for (Product product : dao.getAll().values()) {
	    this.assertEqualProducts(product, dao.get(product.getSku()));
	}
    }

    protected final void doTestSet() {
	Product product = dao.getAll().values().iterator().next();
	this.assertEqualProducts(product, dao.get(product.getSku()));
	product.setName("Updated name");
	dao.set(product);
	this.assertEqualProducts(product, dao.get(product.getSku()));
    }

    protected final void doTestDeleteBySku() {
	Assert.assertEquals(totalProducts, dao.getAll().size());
	dao.delete(dao.getAll().values().iterator().next().getSku());
	Assert.assertEquals(totalProducts - 1, dao.getAll().size());
    }

    protected final void doTestDeleteByProduct() {
	Assert.assertEquals(totalProducts, dao.getAll().size());
	dao.delete(dao.getAll().values().iterator().next());
	Assert.assertEquals(totalProducts - 1, dao.getAll().size());
    }

    protected final void assertEqualProducts(final Product expected,
	    final Product actual) {
	Assert.assertNotNull(expected);
	Assert.assertNotNull(actual);
	Assert.assertEquals(expected.getId(), actual.getId());
	Assert.assertEquals(expected.getSku(), actual.getSku());
	Assert.assertEquals(expected.getName(), actual.getName());
	Assert.assertEquals(expected.getPrice(), actual.getPrice());
    }

}
