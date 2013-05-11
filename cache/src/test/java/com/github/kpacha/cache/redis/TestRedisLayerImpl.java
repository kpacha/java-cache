package com.github.kpacha.cache.redis;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.Jedis;

/**
 * The RedisLayerImpl test
 * 
 * @author kpacha666@gmail.com
 */
public class TestRedisLayerImpl extends Config {

    private static RedisLayerImpl<Object> redis = null;
    private static Integer test1 = new Integer(2);
    private static String test2 = "Sample data";
    private static Float test3 = new Float(2.67);
    private static boolean test4 = true;
    private static char test5 = 'a';
    private static Boolean test6 = false;

    private Jedis jedis = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	List<InetSocketAddress> redisPool = new ArrayList<InetSocketAddress>(1);
	redisPool.add(new InetSocketAddress(redisServerIP, redisServerPort));
	redis = new RedisLayerImpl<Object>(redisPool, Object.class);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
	redis = null;
    }

    @Before
    public void setUp() throws Exception {
	jedis = redis.jedis();
	jedis.flushAll();
	redis.set("test1", 600, test1);
	redis.set("test2", 600, test2);
	redis.set("test3", 600, test3);
	redis.set("test4", 600, test4);
	redis.set("Atest5", 600, test5);
	redis.set("Btest6", 600, test6);

    }

    @After
    public void tearDown() throws Exception {
	jedis.flushAll();
	jedis.disconnect();
	jedis = null;
    }

    @Test
    public final void testIsLocked() {
	redis.lock("test1");
	Assert.assertTrue(redis.isLocked("test1"));
	Assert.assertTrue(!redis.isLocked("test2"));
	Assert.assertTrue(!redis.isLocked("test3"));
	Assert.assertTrue(!redis.isLocked("test4"));
    }

    @Test
    public final void testLock() {
	Assert.assertTrue(!redis.isLocked("test3"));
	redis.lock("test3");
	Assert.assertTrue(redis.isLocked("test3"));

	Assert.assertTrue(!redis.isLocked("test4"));
	redis.lock("test4");
	Assert.assertTrue(redis.isLocked("test4"));
    }

    @Test
    public final void testUnlock() {
	redis.lock("test1");
	Assert.assertTrue(redis.isLocked("test1"));
	redis.unlock("test1");
	Assert.assertTrue(!redis.isLocked("test1"));
    }

}
