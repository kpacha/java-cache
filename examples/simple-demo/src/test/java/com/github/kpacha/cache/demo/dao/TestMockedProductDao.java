package com.github.kpacha.cache.demo.dao;

import org.junit.Before;
import org.junit.Test;

import com.github.kpacha.cache.demo.AbstractTestProductDao;
import com.github.kpacha.cache.demo.memcached.MemcachedMockBuilder;


/**
 * Set up the in-memory stored product DAO implementation
 * 
 * @author kpacha666@gmail.com
 */
public class TestMockedProductDao extends AbstractTestProductDao {

    @Before
    public void setUp() throws Exception {
	totalProducts = 100;
	dao = MemcachedMockBuilder.getMockedProductDao(totalProducts);
    }

    @Test
    public final void testGetAll() {
	doTestGetAll();
    }

    @Test
    public final void testGet() {
	doTestGet();
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
