package com.github.kpacha.cache.demo.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import com.github.kpacha.cache.core.CacheLayer;
import com.github.kpacha.cache.demo.AbstractDemoApp;
import com.github.kpacha.cache.demo.dao.LeafCachedDao;
import com.github.kpacha.cache.demo.dao.NodeCachedDao;
import com.github.kpacha.cache.demo.dao.ProductCachedDao;
import com.github.kpacha.cache.demo.memcached.worker.LeafWriterThread;
import com.github.kpacha.cache.demo.memcached.worker.NodeReaderThread;
import com.github.kpacha.cache.demo.memcached.worker.ProductReaderThread;
import com.github.kpacha.cache.demo.model.Leaf;
import com.github.kpacha.cache.demo.model.Node;
import com.github.kpacha.cache.demo.model.Product;
import com.github.kpacha.cache.memcached.MemcachedLayerImpl;

/**
 * The memcached demo app.
 * 
 * It just creates several workers with the required configuration parameters
 * and enjoys the show
 * 
 * @author kpacha666@gmail.com
 */
public class MemcachedDemoApp extends AbstractDemoApp {

    /**
     * Default memcached server configuration
     */
    protected static final String MEMCACHED_IP = "127.0.0.1";
    protected static final int MEMCACHED_PORT = 11211;

    /**
     * Default memcached server pool
     */
    public static List<InetSocketAddress> memcachedPool = Arrays
	    .asList(new InetSocketAddress(MEMCACHED_IP, MEMCACHED_PORT));

    /**
     * The cached-enabled DAO's
     */
    protected ProductCachedDao productMemcachedDao = null;
    protected NodeCachedDao nodeMemcachedDao = null;
    protected LeafCachedDao leafMemcachedDao = null;

    /**
     * Set up the product cached dao
     * 
     * @throws IOException
     */
    @Override
    protected void setUpProducts() throws IOException {
	super.setUpProducts();
	final CacheLayer<Product> productCacheLayer = new MemcachedLayerImpl<Product>(
		memcachedPool);
	productMemcachedDao = new ProductCachedDao(productCacheLayer,
		productDbDao);
    }

    /**
     * Set up the node cached dao
     * 
     * @throws IOException
     */
    @Override
    protected void setUpNodes() throws IOException {
	super.setUpNodes();
	final CacheLayer<Node> nodeCache = new MemcachedLayerImpl<Node>(
		memcachedPool);
	nodeMemcachedDao = new NodeCachedDao(nodeCache, nodeDbDao);
    }

    /**
     * Set up the leaf cached dao
     * 
     * @throws IOException
     */
    @Override
    protected void setUpLeaves() throws IOException {
	super.setUpLeaves();
	final CacheLayer<Leaf> leafCache = new MemcachedLayerImpl<Leaf>(
		memcachedPool);
	leafMemcachedDao = new LeafCachedDao(leafCache, leafDbDao);
    }

    /**
     * Add a product worker to the thread list
     * 
     * @param i
     *            The worker id
     */
    @Override
    protected void attachProductWorker(int i) {
	threads.add(new ProductReaderThread(Integer.toString(i))
		.setProductMemcachedDao(productMemcachedDao).setProducts(
			products));
    }

    /**
     * Add a node worker to the thread list
     * 
     * @param i
     *            The worker id
     */
    @Override
    protected void attachNodeWorker(int i) {
	threads.add(new NodeReaderThread(Integer.toString(i))
		.setNodeMemcachedDao(nodeMemcachedDao).setNodes(
			nodes.subList(0, nodes.size())));
    }

    /**
     * Add a leaf worker to the thread list
     * 
     * @param i
     *            The worker id
     */
    @Override
    protected void attachLeafWorker(int i) {
	threads.add(new LeafWriterThread(String.valueOf(i))
		.setLeafMemcachedDao(leafMemcachedDao).setLeaves(leaves));
    }

    /**
     * The dummy constructor
     * 
     * @param str
     * @throws IOException
     */
    public MemcachedDemoApp(String str) throws IOException {
	super(str);
    }

    /**
     * Instantiate the app an start it!
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
	AbstractDemoApp app = new MemcachedDemoApp("simple demo");
	app.start();
    }
}
