package com.github.kpacha.cache.demo.memcached.worker;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.github.kpacha.cache.demo.model.Leaf;
import com.github.kpacha.cache.demo.model.Node;


/**
 * Node writer worker
 * 
 * @author kpacha666@gmail.com
 */
public class NodeWriterThread extends AbstractCompositeThread {

    public NodeWriterThread(String name) {
	super(name);
	this.totalLoops = 1;
	totalEntities = 1000;
	nodes = new ArrayList<Node>((int) totalEntities);
	for (int i = 0; i < totalEntities; i++) {
	    Node node = new Node();
	    double totalLeaves = Math.abs((double) generator.nextInt(10));
	    node.setName(this.getClass().getSimpleName() + "_"
		    + generator.nextInt() + "_" + totalLeaves);
	    final Set<Leaf> leaves = new HashSet<Leaf>((int) totalLeaves);
	    for (int j = 0; j < totalLeaves; j++) {
		Leaf leaf = new Leaf();
		leaf.setEmail(node.getName() + "_" + i + "@mailinator.com");
		leaves.add(leaf);
	    }
	    node.setLeafs(leaves);
	    nodes.add(node);
	}
    }

    @Override
    protected void doLoop() {
	for (Node node : nodes) {
	    long startTime = new Date().getTime();
	    nodeMemcachedDao.set(node);
	    checkIterationDuration(node.getName(), new Date().getTime()
		    - startTime);
	}
    }

}
