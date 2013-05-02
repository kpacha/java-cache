package com.github.kpacha.cache.demo.memcached.worker;

import java.util.ArrayList;
import java.util.List;

import com.github.kpacha.cache.core.Dao;
import com.github.kpacha.cache.demo.dao.LeafCachedDao;
import com.github.kpacha.cache.demo.model.Leaf;
import com.github.kpacha.cache.demo.model.Node;
import com.github.kpacha.cache.demo.worker.AbstractThread;


/**
 * Base composite-related worker
 * 
 * @author kpacha666@gmail.com
 */
public abstract class AbstractCompositeThread extends AbstractThread {

    protected Dao<Node> nodeMemcachedDao;
    protected List<Node> nodes = new ArrayList<Node>(0);
    protected Dao<Leaf> leafMemcachedDao;
    protected List<Leaf> leaves = new ArrayList<Leaf>(0);

    public AbstractCompositeThread(final String name) {
	super(name);
	this.totalLoops = 2;
    }

    /**
     * @param nodeMemcachedDao
     *            the nodeMemcachedDao to set
     */
    public AbstractCompositeThread setNodeMemcachedDao(
	    Dao<Node> nodeMemcachedDao) {
	this.nodeMemcachedDao = nodeMemcachedDao;
	return this;
    }

    /**
     * @param nodes
     *            the nodes to set
     */
    public AbstractCompositeThread setNodes(List<Node> nodes) {
	this.nodes = nodes;
	calculateTotalEntites();
	return this;
    }

    public AbstractCompositeThread setLeafMemcachedDao(
	    LeafCachedDao leafMemcachedDao) {
	this.leafMemcachedDao = leafMemcachedDao;
	return this;
    }

    public AbstractCompositeThread setLeaves(List<Leaf> leaves) {
	this.leaves = leaves;
	calculateTotalEntites();
	return this;
    }

    protected void calculateTotalEntites() {
	this.totalEntities = nodes.size() + leaves.size();
    }
}
