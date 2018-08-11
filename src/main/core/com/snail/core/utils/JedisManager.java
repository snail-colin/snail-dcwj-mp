package com.snail.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public class JedisManager {

	//Redis服务器IP
	private static String ADDR = "127.0.0.1";

	//Redis的端口号
	private static String PORT = "6379";

	//访问密码
	private static String AUTH =  "AFiAkeYtoH";

	//可用连接实例的最大数目，默认值为8；
	//如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
	private static int MAX_ACTIVE = 20;

	//控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
	private static int MAX_IDLE = 2;

	//等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
	private static int MAX_WAIT = 10000;

	private static int TIMEOUT = 10000;

	//在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
	private static boolean TEST_ON_BORROW = true;

	private static JedisPool jedisPool = null;

	static Properties properties = new Properties();
	
	/**
	 * 初始化Redis连接池
	 */
	static {
		InputStream is = null;
		String redisName = "/config/snail-" + System.getProperty("server.run.type","test") + ".properties";
		try {
			is = JedisManager.class.getClassLoader().getResourceAsStream(redisName);
			properties.load(is);
			ADDR = properties.getProperty("CACHE_HOST");
			PORT = properties.getProperty("CACHE_PORT");
			AUTH = properties.getProperty("CACHE_PWD");
			
			JedisPoolConfig config = new JedisPoolConfig();
			config = new JedisPoolConfig();
			config.setMaxTotal(MAX_ACTIVE);
			config.setMaxIdle(MAX_IDLE);
			config.setMaxWaitMillis(MAX_WAIT);
			config.setTestOnBorrow(TEST_ON_BORROW);
			jedisPool = new JedisPool(config, ADDR, Integer.parseInt(PORT), TIMEOUT, AUTH, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 获取Jedis实例
	 * @return
	 */
	public synchronized static Jedis getJedis() {
		try {
			if (jedisPool != null) {
				Jedis resource = jedisPool.getResource();
				return resource;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	  public JedisPool getJedisPool() {
	        return jedisPool;
	    }

	    public void setJedisPool(JedisPool jedisPool) {
	        JedisManager.jedisPool = jedisPool;
	    }
	    
	/**
	 * 释放jedis资源
	 * @param jedis
	 */
	public static void returnResource(final Jedis jedis) {
		if (jedis != null) {
			jedisPool.returnResourceObject(jedis);
		}
	}

    public static void returnResource(Jedis jedis, boolean isBroken) {
        if (jedis == null)
            return;
        jedis.close();
    }

 
}
