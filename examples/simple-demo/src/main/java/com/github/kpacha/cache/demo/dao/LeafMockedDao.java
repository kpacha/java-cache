package com.github.kpacha.cache.demo.dao;

import com.github.kpacha.cache.core.Dao;
import com.github.kpacha.cache.demo.model.Leaf;

/**
 * Sample leaf dao with a local store
 * 
 * @see Dao
 * @see AbstractMockedDao
 * @author kpacha666@gmail.com
 */
public class LeafMockedDao extends AbstractMockedDao<Leaf> implements
	Dao<Leaf> {

    public boolean set(final Leaf leaf) {
	catalog.put(leaf.getEmail(), leaf);
	return true;
    }

    public boolean delete(final Leaf leaf) {
	return this.delete(leaf.getEmail());
    }

}
