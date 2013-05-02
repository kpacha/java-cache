package com.github.kpacha.cache.demo;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.github.kpacha.cache.core.Dao;
import com.github.kpacha.cache.demo.dao.AbstractMockedDao;
import com.github.kpacha.cache.demo.dao.LeafMockedDao;
import com.github.kpacha.cache.demo.dao.NodeMockedDao;
import com.github.kpacha.cache.demo.dao.ProductMockedDao;
import com.github.kpacha.cache.demo.model.Leaf;
import com.github.kpacha.cache.demo.model.Node;
import com.github.kpacha.cache.demo.model.Product;

/**
 * Simple mocked components builder
 * 
 * @author kpacha666@gmail.com
 */
public class MockBuilder {

    protected static Random generator = new Random();

    public static ProductMockedDao getMockedProductDao(final int totalProducts) {
	final ProductMockedDao mockedDao = new ProductMockedDao();
	for (int i = 0; i < totalProducts; i++) {
	    mockedDao.set(getMockedProduct());
	}
	return mockedDao;
    }

    public static Dao<Node> getMockedNodeDao(final int totalNodes,
	    final int minLeafs) {
	final AbstractMockedDao<Node> nodeDao = new NodeMockedDao();
	for (int i = 0; i < totalNodes; i++) {
	    nodeDao.set(getMockedNode(minLeafs + i));
	}
	return nodeDao;
    }

    public static Dao<Leaf> getMockedLeafDao(final int totalLeafs) {
	final AbstractMockedDao<Leaf> leafDao = new LeafMockedDao();
	for (int i = 0; i < totalLeafs; i++) {
	    leafDao.set(getMockedLeaf(Math.abs(generator.nextInt())));
	}
	return leafDao;
    }

    public static Product getMockedProduct() {
	Product product = new Product();
	product.setId(generator.nextInt());
	product.setName("mock product " + product.getId());
	product.setSku("mockProduct" + product.getId());
	product.setPrice(generator.nextDouble() * 100);
	return product;
    }

    public static Node getMockedNode(final int totalLeafs) {
	final Node node = new Node();
	node.setName("node" + Math.abs(generator.nextInt()) + "-" + totalLeafs);
	final Set<Leaf> leafs = new HashSet<Leaf>();
	for (int i = 0; i < totalLeafs; i++) {
	    leafs.add(getMockedLeaf(i));
	}
	node.setLeafs(leafs);
	return node;
    }

    public static Leaf getMockedLeaf(final int id) {
	final Leaf leaf = new Leaf();
	leaf.setEmail("sample" + Math.abs(generator.nextInt()) + "_" + id
		+ "@mailinator.com");
	return leaf;
    }
}
