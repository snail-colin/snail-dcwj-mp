package com.snail.core.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import redis.clients.jedis.Jedis;

/**
 * 缓存工具
 * 
 */
public class CacheUtil {

	private final static String CACHE = "media_cache";

	/**
	 * 路径分隔符
	 */
	private final static String PATH_SEPARATOR = "/";

	/**
	 * 获取某个类型下的所有缓存
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> Map<String, T> getCaches(Class<T> clazz) {
		Jedis jedis = JedisManager.getJedis();
		String key = CACHE + PATH_SEPARATOR + clazz.getName();
		Map<String, String> values = jedis.hgetAll(key);
		Map<String, T> result = new HashMap<String, T>();
		if (values != null && values.size() > 0) {
			for (Map.Entry<String, String> entry : values.entrySet())
				result.put(entry.getKey(), ConvertorUtil.jsonToObject(clazz, entry.getValue()));
		}
		return result;
	}

	/**
	 * @desc 获取缓存
	 * @param clazz
	 * @param field
	 * @return
	 */
	public static <T> T getCache(Class<T> clazz, String field) {
		Jedis jedis = JedisManager.getJedis();
		String key = CACHE + PATH_SEPARATOR + clazz.getName();
		Map<String, String> values = jedis.hgetAll(key);
		jedis.close();
		if (values != null && values.size() > 0 && values.containsKey(field)) {
			String value = values.get(field);
			return ConvertorUtil.jsonToObject(clazz, value);
		}
		return null;
	}

	/**
	 * @desc 删除缓存
	 * @param clazz
	 * @param field
	 */
	public static <T> void delCache(Class<T> clazz, String field) {
		Jedis jedis = JedisManager.getJedis();
		String key = CACHE + PATH_SEPARATOR + clazz.getName();
		jedis.hdel(key, field);
		jedis.close();
	}


	/**
	 * 更新缓存
	 * @param field
	 * @param time 单位为：秒
	 * @param obj
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static void updateCache(String field, Integer time, Object obj)
			throws JsonGenerationException, JsonMappingException, IOException {
		Jedis jedis = JedisManager.getJedis();
		String key = CACHE + PATH_SEPARATOR + obj.getClass().getName();
		String value = ConvertorUtil.objectToJson(obj).toString();
		if (time != null)
			jedis.expire(key, time);
		jedis.hset(key, field, value);
		jedis.close();
	}

	/********************************* 以下方法用于获取map类型 ********************************/

	/**
	 * 获取缓存
	 * @param clazz
	 * @return
	 */
	public static <T> Map<String, T> getMapCache(Class<T> clazz) {
		Jedis jedis = JedisManager.getJedis();
		String key = CACHE + PATH_SEPARATOR + clazz.getName();
		Map<String, String> values = jedis.hgetAll(key);
		jedis.close();
		Map<String, T> rst = new HashMap<String, T>();
		for (Entry<String, String> object : values.entrySet())
			rst.put(object.getKey(), ConvertorUtil.jsonToObject(clazz, object.getValue()));
		return rst;
	}
	
	/**
	 * 获取缓存
	 * 
	 * @param clazz
	 * @param field
	 * @return
	 */
	public static <T> Map<String, T> getMapCache(Class<T> clazz, String field) {
		Jedis jedis = JedisManager.getJedis();
		String key = CACHE + PATH_SEPARATOR + clazz.getName() + "." + field;
		Map<String, String> values = jedis.hgetAll(key);
		jedis.close();
		Map<String, T> rst = new HashMap<String, T>();
		for (Entry<String, String> object : values.entrySet())
			rst.put(object.getKey(), ConvertorUtil.jsonToObject(clazz, object.getValue()));
		return rst;
	}
	
	
	
	/**
	 * 删除缓存 delAll 为true 删除该key下所有缓存 false时删除map下面指定的key
	 * 
	 * @param clazz
	 * @param field
	 * @param delAll
	 */
	public static <T> void delMapCache(Class<T> clazz, String field, String[] keys) {
		Jedis jedis = JedisManager.getJedis();
		String key = CACHE + PATH_SEPARATOR + clazz.getName() + "." + field;
		if (keys != null && keys.length > 0)
			jedis.hdel(key, keys);
		else
			jedis.del(key);
		jedis.close();
	}

	/**
	 * 更新缓存
	 * 
	 * @param <T>
	 * @param field
	 * @param map
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	@SuppressWarnings("unused")
	private static <T> void updateCache(String field, Integer time, Class<T> clazz, Map<String, T> map)
			throws JsonGenerationException, JsonMappingException, IOException {
		Jedis jedis = JedisManager.getJedis();
		String key = CACHE + PATH_SEPARATOR + clazz.getName() + "." + field;
		Map<String, String> rst = new HashMap<String, String>();
		for (Entry<String, T> object : map.entrySet())
			rst.put(object.getKey(), ConvertorUtil.objectToJson(object.getValue()).toString());
		// 设置过期时间,每次重新重新赋值则刷新该时间 格式为为 ： 60 * 60 * 12
		if (time != null)
			jedis.expire(key, time);
		jedis.hmset(key, rst);
		jedis.close();
	}
	
	/**
	 *  更新缓存
	 * @param field
	 * @param obj
	 */
	public static void updateCache(String field, Object obj) {
		Jedis jedis = JedisManager.getJedis();
		String key = CACHE + PATH_SEPARATOR + obj.getClass().getName();
		String value = ConvertorUtil.pojo2json(obj).toString();
		jedis.hset(key, field, value);
		//jedis.close();
		JedisManager.returnResource(jedis);
	}

	/**
	 * 保存缓存
	 * @param key
	 * @param field
	 * @param value
	 * @param expireTime
	 * @throws Exception
	 */
	public static void saveCaches(byte[] key, byte[] field, byte[] value, int expireTime)
			throws Exception {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = JedisManager.getJedis();
			jedis.hset(key, field, value);
			if (expireTime > 0)
				jedis.expire(key, expireTime);
		} catch (Exception e) {
			isBroken = true;
			throw e;
		} finally {
			JedisManager.returnResource(jedis, isBroken);
		}
	}

	/**
	 * 获取缓存
	 * @param key
	 * @param field
	 * @return
	 * @throws Exception
	 */
	public static byte[] getCaches(byte[] key, byte[] field) throws Exception {
		Jedis jedis = null;
		byte[] result = null;
		boolean isBroken = false;
		try {
			jedis = JedisManager.getJedis();
			result = jedis.hget(key, field);
		} catch (Exception e) {
			isBroken = true;
			throw e;
		} finally {
			JedisManager.returnResource(jedis, isBroken);
		}
		return result;
	}

	/**
	 * 删除缓存
	 * @param key
	 * @param field
	 * @throws Exception
	 */
	public static void deleteCachesByKey(byte[] key, byte[] field) throws Exception {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = JedisManager.getJedis();
			jedis.hdel(key, field);
		} catch (Exception e) {
			isBroken = true;
			throw e;
		} finally {
			JedisManager.returnResource(jedis, isBroken);
		}
	}


	/**
	 * get
	 * @param key
	 * @param requiredType
	 * @return
	 */
	public static <T> T get(String key , Class<T>...requiredType){
		Jedis jds = null;
        boolean isBroken = false;
		try {
			jds = JedisManager.getJedis();
			byte[] skey = key.getBytes();
			return SerializeUtil.deserialize(jds.get(skey),requiredType);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
        	JedisManager.returnResource(jds, isBroken);
        }
		return null;
	}
	/**
	 * set
	 * @param key
	 * @param value
	 */
	public static void set(String key ,Object value){
		Jedis jds = null;
        boolean isBroken = false;
		try {
			jds = JedisManager.getJedis();
			byte[] skey = key.getBytes();
			byte[] svalue = SerializeUtil.serialize(value);
			jds.set(skey, svalue);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
        	JedisManager.returnResource(jds, isBroken);
        }
	}
	/**
	 * 过期时间的set
	 * @param key
	 * @param value
	 * @param timer （秒）
	 */
	public static void setex(String key, Object value, int timer) {
		Jedis jds = null;
        boolean isBroken = false;
		try {
			jds = JedisManager.getJedis();
			byte[] skey = key.getBytes();
			byte[] svalue = SerializeUtil.serialize(value);
			jds.setex(skey, timer, svalue);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
        	JedisManager.returnResource(jds, isBroken);
        }
		
	}

	
}
