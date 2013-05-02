package com.github.kpacha.cache.demo.memcached.worker;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.github.kpacha.cache.demo.model.Leaf;


/**
 * Leaf reader worker
 * 
 * @author kpacha666@gmail.com
 */
public class LeafReaderThread extends AbstractCompositeThread {

    public LeafReaderThread(String name) {
	super(name);
    }

    @Override
    protected void doLoop() {
	List<Leaf> leaves = this.leaves.subList(0, this.leaves.size());
	Collections.shuffle(leaves);
	for (Leaf leaf : leaves) {
	    long startTime = new Date().getTime();
	    Leaf receivedLeaf = leafMemcachedDao.get(leaf.getEmail());
	    checkIterationDuration(leaf.getEmail(), new Date().getTime()
		    - startTime);
	    checkIterationResult(leaf.getEmail(), leaf, receivedLeaf);
	}
    }

}
