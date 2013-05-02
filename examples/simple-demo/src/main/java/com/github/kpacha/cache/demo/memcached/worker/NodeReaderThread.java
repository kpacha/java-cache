package com.github.kpacha.cache.demo.memcached.worker;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.github.kpacha.cache.demo.model.Node;


/**
 * Node reader worker
 * 
 * @author kpacha666@gmail.com
 */
public class NodeReaderThread extends AbstractCompositeThread {

    public NodeReaderThread(String name) {
	super(name);
    }

    @Override
    protected void doLoop() {
	List<Node> nodes = this.nodes.subList(0, this.nodes.size());
	Collections.shuffle(nodes);
	for (Node node : nodes) {
	    long startTime = new Date().getTime();
	    Node receivedNode = nodeMemcachedDao.get(node.getName());
	    checkIterationDuration(node.getName(), new Date().getTime()
		    - startTime);
	    checkIterationResult(node.getName(), node, receivedNode);
	}
    }

}
