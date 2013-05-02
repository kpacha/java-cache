package com.github.kpacha.cache.demo.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * Simple node pojo
 * 
 * @author kpacha666@gmail.com
 */
public class Node implements Serializable {

    private static final long serialVersionUID = 4606816827964455312L;

    protected String name;
    protected Set<Leaf> leafs = new HashSet<Leaf>(0);
    protected Set<Node> nodes = new HashSet<Node>(0);

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @return the leafs
     */
    public Set<Leaf> getLeafs() {
	return leafs;
    }

    /**
     * @return the nodes
     */
    public Set<Node> getNodes() {
	return nodes;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * @param leafs
     *            the leafs to set
     */
    public void setLeafs(Set<Leaf> leafs) {
	this.leafs = leafs;
    }

    /**
     * @param nodes
     *            the nodes to set
     */
    public void setNodes(Set<Node> nodes) {
	this.nodes = nodes;
    }
}
