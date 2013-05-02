package com.github.kpacha.cache.core;

import java.util.Map;

/**
 * Generic Dao interface
 * 
 * @param <T>
 * @author kpacha666@gmail.com
 */
public interface Dao<T extends Object> {

    /**
     * Get all the stored entities
     * 
     * @return
     */
    Map<String, T> getAll();

    /**
     * Get an entity from the store by its id
     * 
     * @param entityId
     * @return
     */
    T get(final String entityId);

    /**
     * Add or update an entity in the store
     * 
     * @param entity
     * @return
     */
    boolean set(final T entity);

    /**
     * Add or update an entity in the store
     * 
     * @param entityId
     * @param entity
     * @return
     */
    boolean set(final String entityId, final T entity);

    /**
     * Remove an entity from the store
     * 
     * @param entity
     * @return
     */
    boolean delete(final T entity);

    /**
     * Remove an entity from the store by its id
     * 
     * @param entityId
     * @return
     */
    boolean delete(final String entityId);
}
