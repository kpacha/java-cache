package com.github.kpacha.cache.demo.model;

import java.io.Serializable;

/**
 * Simple product pojo
 * 
 * @author kpacha666@gmail.com
 */
public class Product implements Serializable {

    private static final long serialVersionUID = 9128362284583874253L;

    private int id;
    private String name;
    private String sku;
    private double price;

    /**
     * @return the id
     */
    public int getId() {
	return id;
    }

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @return the sku
     */
    public String getSku() {
	return sku;
    }

    /**
     * @return the price
     */
    public double getPrice() {
	return price;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
	this.id = id;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * @param sku
     *            the sku to set
     */
    public void setSku(String sku) {
	this.sku = sku;
    }

    /**
     * @param d
     *            the price to set
     */
    public void setPrice(double d) {
	this.price = d;
    }

}
