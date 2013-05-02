package com.github.kpacha.cache.demo.worker;

import java.util.Date;
import java.util.Random;

/**
 * Base worker
 * 
 * @author kpacha666@gmail.com
 */
public abstract class AbstractThread extends Thread {

    private int fromCache = 0;
    private int fromDb = 0;
    protected int totalLoops = 1;
    protected double totalEntities = 0;
    protected Random generator = new Random();

    private static int processedEntities = 0;
    private static int longIterations = 0;
    private static int nullResults = 0;

    private static int totalThreads = 0;

    public AbstractThread(final String name) {
	super(name);
	synchronized (this) {
	    totalThreads++;
	}
    }

    public void run() {
	for (int i = 1; i < totalLoops + 1; i++) {
	    fromCache = 0;
	    fromDb = 0;

	    long startTime = new Date().getTime();
	    log("Starting the loop " + i);
	    doLoop();
	    incrementProcessedEntities(fromCache + fromDb);

	    long loopDuration = new Date().getTime() - startTime;
	    log("Loop " + i + " finished. Elapsed: " + loopDuration + "ms. "
		    + (loopDuration / totalEntities)
		    + " ms per request. Processed entities: " + totalEntities);
	    if (fromCache + fromDb != 0) {
		log("From db:" + fromDb + ", from cache:" + fromCache + ". ["
			+ (fromDb + fromCache) + "] Hit rate: "
			+ (fromCache * 100 / (fromCache + fromDb)) + "%");
	    }
	}
	log("The thread " + getName() + " is finished!");
	synchronized (this) {
	    totalThreads--;
	    if (totalThreads == 0) {
		log("Processed entities: " + processedEntities);
		log("Long iterations: " + longIterations);
		log("Null results: " + nullResults);
		System.exit(0);
	    }
	}
    }

    abstract protected void doLoop();

    protected void log(final String msg) {
	System.out.println(new Date() + " : " + getClass().getSimpleName()
		+ " " + getName() + " - " + msg);
    }

    protected static synchronized void incrementProcessedEntities(
	    final int increment) {
	processedEntities += increment;
    }

    protected static synchronized void incrementLongIterations() {
	longIterations++;
    }

    protected static synchronized void incrementNullResults() {
	nullResults++;
    }

    protected void checkIterationResult(final String entityId,
	    final Object extepctedEntity, final Object receivedEntity) {
	if (receivedEntity == null) {
	    log("=========================> Not regular iteration: " + entityId
		    + ": " + " not found!");
	    incrementNullResults();
	} else {
	    if (extepctedEntity.equals(receivedEntity)) {
		fromDb++;
	    } else {
		fromCache++;
	    }
	}
    }

    protected void checkIterationDuration(final String entityId,
	    final long elapsed) {
	if (elapsed > 5000) {
	    log("=========================> Not regular iteration: [" + elapsed
		    + "ms] for " + entityId);
	    incrementLongIterations();
	}
    }

}
