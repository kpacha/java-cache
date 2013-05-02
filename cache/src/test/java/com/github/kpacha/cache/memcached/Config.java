package com.github.kpacha.cache.memcached;

/**
 * Default class for all the memcached-related tests
 * 
 * @author kpacha666@gmail.com
 */
public class Config {

    /**
     * Default memcached server configuration
     */
    protected static String memcachedServerIP = "127.0.0.1";
    protected static int memcachedServerPort = 11211;

    /**
     * Set up the memcached server connection with the parametrized IP and port
     * (testing with maven) or with the default values (for raw test execution)
     */
    public Config() {
	final String memcachedServerIP = System
		.getProperty("memcachedServer.IP");
	final String memcachedServerPort = System
		.getProperty("memcachedServer.Port");
	if (memcachedServerIP != null && !memcachedServerIP.equals("")) {
	    Config.memcachedServerIP = memcachedServerIP;
	}
	if (memcachedServerPort != null && !memcachedServerPort.equals("")) {
	    Config.memcachedServerPort = Integer.parseInt(memcachedServerPort);
	}
    }

}
