package com.github.kpacha.cache.redis;

/**
 * Default class for all the redis-related tests
 * 
 * @author kpacha666@gmail.com
 */
public class Config {

    /**
     * Default redis server configuration
     */
    protected static String redisServerIP = "127.0.0.1";
    protected static int redisServerPort = 6379;

    /**
     * Set up the redis server connection with the parametrized IP and port
     * (testing with maven) or with the default values (for raw test execution)
     */
    public Config() {
	final String redisServerIP = System.getProperty("redisServer.IP");
	final String redisServerPort = System.getProperty("redisServer.Port");
	if (redisServerIP != null && !redisServerIP.equals("")) {
	    Config.redisServerIP = redisServerIP;
	}
	if (redisServerPort != null && !redisServerPort.equals("")) {
	    Config.redisServerPort = Integer.parseInt(redisServerPort);
	}
    }

}
