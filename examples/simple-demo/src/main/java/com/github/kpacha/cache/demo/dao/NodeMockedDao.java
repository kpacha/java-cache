package com.github.kpacha.cache.demo.dao;

import com.github.kpacha.cache.core.Dao;
import com.github.kpacha.cache.demo.model.Node;

/**
 * Sample node dao with a local store
 * 
 * @see Dao
 * @see AbstractMockedDao
 * @author kpacha666@gmail.com
 */
public class NodeMockedDao extends AbstractMockedDao<Node> implements
	Dao<Node> {

    public boolean set(final Node node) {
	catalog.put(node.getName(), node);
	return true;
    }

    public boolean delete(final Node node) {
	return this.delete(node.getName());
    }

}
