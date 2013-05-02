package com.github.kpacha.cache.demo.dao;

import java.net.InetSocketAddress;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.kpacha.cache.demo.AbstractTestProductDao;
import com.github.kpacha.cache.demo.memcached.MemcachedMockBuilder;
import com.github.kpacha.cache.demo.model.Product;
import com.github.kpacha.cache.memcached.ExtendedMemcachedClient;
import com.github.kpacha.cache.memcached.ExtendedMemcachedClientImpl;
import com.github.kpacha.cache.memcached.MemcachedLayerImpl;

/**
 * Set up the cached-enhanced product DAOs implementation
 * 
 * @author kpacha666@gmail.com
 */
public class TestCachedProductDao extends AbstractTestProductDao {

    private static ExtendedMemcachedClient memcache = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	memcache = new ExtendedMemcachedClientImpl(new InetSocketAddress(
		memcachedServerIP, memcachedServerPort));
    }

    @Before
    public void setUp() throws Exception {
	totalProducts = 100;
	dao = new ProductCachedDao(new MemcachedLayerImpl<Product>(),
		MemcachedMockBuilder.getMockedProductDao(totalProducts));
	for (final String key : memcache.getKeys()) {
	    memcache.delete(key);
	}
	synchronized (this) {
	    this.wait(100);// sleep for 100 ms
	}
    }

    @Test
    public final void testGetAll() {
	// test with empty cache
	doTestGetAll();
	// now, get all from the cache!
	doTestGetAll();
    }

    @Test
    public final void testGet() {
	// test with empty cache
	doTestGet();
	// now, get all from the cache!
	doTestGet();
    }

    @Test
    public final void testGetWithLocking() {
	final Product product = dao.getAll().values().iterator().next();
	memcache.set("locked:Product:" + product.getSku(), 60, true);
	dao.delete(product);
	Assert.assertEquals(null, dao.get(product.getSku()));
	Assert.assertEquals(true,
		memcache.get("locked:Product:" + product.getSku()));
    }

    @Test
    public final void testSet() {
	doTestSet();
    }

    @Test
    public final void testDeleteBySku() {
	doTestDeleteBySku();
    }

    @Test
    public final void testDeleteByProduct() {
	doTestDeleteByProduct();
    }

}
