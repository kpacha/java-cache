package com.github.kpacha.cache.demo.memcached.worker;

import java.util.ArrayList;
import java.util.Date;

import com.github.kpacha.cache.demo.memcached.MemcachedMockBuilder;
import com.github.kpacha.cache.demo.model.Leaf;


/**
 * Leaf writer worker
 * 
 * @author kpacha666@gmail.com
 */
public class LeafWriterThread extends AbstractCompositeThread {

    public LeafWriterThread(String name) {
	super(name);
	this.totalLoops = 1;
	totalEntities = 10000;
	leaves = new ArrayList<Leaf>((int) totalEntities);
	for (int i = 0; i < totalEntities; i++) {
	    Leaf leaf = MemcachedMockBuilder.getMockedLeaf(i);
	    leaf.setComment(this.getClass().getSimpleName() + "_"
		    + leaf.getEmail());
	    leaves.add(leaf);
	}
    }

    @Override
    protected void doLoop() {
	for (Leaf leaf : leaves) {
	    long startTime = new Date().getTime();
	    leafMemcachedDao.set(leaf);
	    checkIterationDuration(leaf.getEmail(), new Date().getTime()
		    - startTime);
	}
    }

}
