package com.github.kpacha.cache.demo.model;

import java.io.Serializable;

/**
 * Simple leaf pojo
 * 
 * @author kpacha666@gmail.com
 */
public class Leaf implements Serializable {

    private static final long serialVersionUID = 2065258417102032478L;

    private String email;
    private String comment;

    /**
     * @return the email
     */
    public String getEmail() {
	return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
	this.email = email;
    }

    /**
     * @return the comment
     */
    public String getComment() {
	return comment;
    }

    /**
     * @param comment
     *            the comment to set
     */
    public void setComment(String comment) {
	this.comment = comment;
    }

}
