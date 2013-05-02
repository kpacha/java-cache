package com.github.kpacha.cache.memcached;

import java.net.InetSocketAddress;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.kpacha.cache.core.Mutex;
import com.github.kpacha.cache.memcached.ExtendedMemcachedClient;
import com.github.kpacha.cache.memcached.ExtendedMemcachedClientImpl;

/**
 * The ExtendedMemecachedClient test
 * 
 * @author kpacha666@gmail.com
 */
public class TestExtendedMemcachedClient extends Config {

    private static ExtendedMemcachedClient memcache = null;
    private static Integer test1 = new Integer(2);
    private static String test2 = "Sample data";
    private static Float test3 = new Float(2.67);
    private static boolean test4 = true;
    private static char test5 = 'a';
    private static Boolean test6 = false;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

	memcache = new ExtendedMemcachedClientImpl(new InetSocketAddress(
		memcachedServerIP, memcachedServerPort));
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
	memcache = null;
    }

    @Before
    public void setUp() throws Exception {
	for (String key : memcache.getKeys()) {
	    memcache.delete(key);
	}
	memcache.add("test1", 600, test1);
	memcache.add("test2", 600, test2);
	memcache.add("test3", 600, test3);
	memcache.add("test4", 600, test4);
	memcache.add("Atest5", 600, test5);
	memcache.add("Btest6", 600, test6);

	memcache.lock("test1");
	memcache.add(Mutex.MUTEX_KEY_PREFIX
		+ ExtendedMemcachedClient.KEY_SEPARATOR + "test2", 600, true);
	memcache.add(Mutex.MUTEX_KEY_PREFIX
		+ ExtendedMemcachedClient.KEY_SEPARATOR + "test3", 600, false);
    }

    @After
    public void tearDown() throws Exception {
	// memcache.flush();
	for (String key : memcache.getKeys()) {
	    memcache.delete(key);
	}
    }

    @Test
    public final void testGetKeys() {
	Assert.assertEquals(9, memcache.getKeys().size());
    }

    @Test
    public final void testSelectKeys() {
	Assert.assertEquals(4, memcache.selectKeys("tes.*").size());
	Assert.assertEquals(9, memcache.selectKeys(".*s..").size());
	Assert.assertEquals(2, memcache.selectKeys("(A|B)test.").size());
	Assert.assertEquals(9, memcache.selectKeys(".*").size());
	Assert.assertEquals(1, memcache.selectKeys("test1").size());
	Assert.assertEquals(0, memcache.selectKeys("Unknown").size());
    }

    @Test
    public final void testIsLocked() {
	Assert.assertTrue(memcache.isLocked("test1"));
	Assert.assertTrue(memcache.isLocked("test2"));
	Assert.assertTrue(!memcache.isLocked("test3"));
	Assert.assertTrue(!memcache.isLocked("test4"));
    }

    @Test
    public final void testLock() {
	Assert.assertTrue(!memcache.isLocked("test3"));
	memcache.lock("test3");
	Assert.assertTrue(memcache.isLocked("test3"));

	Assert.assertTrue(!memcache.isLocked("test4"));
	memcache.lock("test4");
	Assert.assertTrue(memcache.isLocked("test4"));
    }

    @Test
    public final void testUnlock() {
	Assert.assertTrue(memcache.isLocked("test1"));
	memcache.unlock("test1");
	Assert.assertTrue(!memcache.isLocked("test1"));

	Assert.assertTrue(memcache.isLocked("test2"));
	memcache.unlock("test2");
	Assert.assertTrue(!memcache.isLocked("test2"));
    }

}
