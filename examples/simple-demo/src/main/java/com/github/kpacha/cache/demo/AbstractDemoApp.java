package com.github.kpacha.cache.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.kpacha.cache.core.Dao;
import com.github.kpacha.cache.demo.model.Leaf;
import com.github.kpacha.cache.demo.model.Node;
import com.github.kpacha.cache.demo.model.Product;
import com.github.kpacha.cache.demo.worker.AbstractThread;

/**
 * The dummy demo app template.
 * 
 * It just creates several workers with the required configuration parameters
 * and enjoys the show
 * 
 * @author kpacha666@gmail.com
 */
public abstract class AbstractDemoApp extends Thread {

    /**
     * Some configuration params
     */
    protected final int totalProducts = 4000;
    protected final int totalNodes = 1500;
    protected final int minLeavesPerNode = 1;
    protected final int totalLeaves = 10000;
    protected final int totalSets = 3;

    /**
     * The DAO's and data collections
     */
    protected Dao<Product> productDbDao = null;
    protected List<Product> products = null;

    protected Dao<Node> nodeDbDao = null;
    protected List<Node> nodes = null;

    protected Dao<Leaf> leafDbDao = null;
    protected List<Leaf> leaves = null;

    /**
     * The workers list
     */
    protected List<AbstractThread> threads = null;

    /**
     * Set up the demo app.
     * 
     * @param args
     * @throws IOException
     */
    public AbstractDemoApp(String str) throws IOException {
	super(str);
	initDemoApp();
    }

    /**
     * First of all, it creates a mocked set of DAOs for all the entities
     * (products, nodes and leafs). Each DAO is created with a collection of
     * sample data. The extensions are able to instantiate the required cache
     * layers and the cached-enhanced DAOs.
     * 
     * Once it have all the required services and components, it prepares a
     * collection of workers to perform the demo.
     * 
     * @throws IOException
     */
    private void initDemoApp() throws IOException {
	// Set up the DAO's
	setUpProducts();
	setUpNodes();
	setUpLeaves();

	// Set up the workers
	threads = new ArrayList<AbstractThread>();
	for (int i = 0; i < totalSets; i++) {
	    attachProductWorker(i);
	    attachNodeWorker(i + totalSets);
	    attachLeafWorker(i + 2 * totalSets);
	    attachLeafWorker(i + 3 * totalSets);
	    attachNodeWorker(i + 4 * totalSets);
	    attachProductWorker(i + 5 * totalSets);
	}
    }

    /**
     * Set up the product collection and the related mocked-db-dao
     * 
     * @throws IOException
     */
    protected void setUpProducts() throws IOException {
	productDbDao = MockBuilder.getMockedProductDao(totalProducts);
	products = new ArrayList<Product>(productDbDao.getAll().values());
    }

    /**
     * Set up the node collection and the related mocked-db-dao
     * 
     * @throws IOException
     */
    protected void setUpNodes() throws IOException {
	nodeDbDao = MockBuilder.getMockedNodeDao(totalNodes, minLeavesPerNode);
	nodes = new ArrayList<Node>(nodeDbDao.getAll().values());
    }

    /**
     * Set up the leaf collection and the related mocked-db-dao
     * 
     * @throws IOException
     */
    protected void setUpLeaves() throws IOException {
	leafDbDao = MockBuilder.getMockedLeafDao(totalLeaves);
	leaves = new ArrayList<Leaf>(leafDbDao.getAll().values());
    }

    /**
     * Add a product worker to the thread list
     * 
     * @param i
     *            The worker id
     */
    abstract protected void attachProductWorker(int i);

    /**
     * Add a node worker to the thread list
     * 
     * @param i
     *            The worker id
     */
    abstract protected void attachNodeWorker(int i);

    /**
     * Add a leaf worker to the thread list
     * 
     * @param i
     *            The worker id
     */
    abstract protected void attachLeafWorker(int i);

    /**
     * Run'em'all!
     * 
     * It just starts all the created threads
     */
    public void start() {
	for (Thread thread : threads) {
	    thread.start();
	}
    }
}
