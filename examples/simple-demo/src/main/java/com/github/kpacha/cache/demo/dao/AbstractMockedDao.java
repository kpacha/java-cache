package com.github.kpacha.cache.demo.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.kpacha.cache.core.Dao;



/**
 * Sample product dao with a local store
 * 
 * @see Dao
 * @author kpacha666@gmail.com
 */
public class AbstractMockedDao<T> implements Dao<T> {
    protected Map<String, T> catalog = new ConcurrentHashMap<String, T>();

    public Map<String, T> getAll() {
	return catalog;
    }

    public T get(final String id) {
	return catalog.get(id);
    }

    public boolean delete(final String id) {
	catalog.remove(id);
	return true;
    }

    @Override
    public boolean set(T entity) {
	return set(entity.toString(), entity);
    }

    @Override
    public boolean set(String entityId, T entity) {
	catalog.put(entity.toString(), entity);
	return true;
    }

    @Override
    public boolean delete(T entity) {
	catalog.remove(entity);
	return true;
    }
}
