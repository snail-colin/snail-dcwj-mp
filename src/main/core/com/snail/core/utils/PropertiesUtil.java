package com.snail.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertiesUtil {
	static Properties properties = new Properties();
	
	/**
	 * 平台连接
	 */
	public static String MP_URL ;
	/**
	 * 平台appid
	 */
	public static String MP_APPID ;
	/**
	 * 平台token
	 */
	public static String MP_PLATFORM_TOKEN;
	
	static {
		InputStream is = null;
		String name = "/config/snail-"  + System.getProperty("server.run.type", "test") + ".properties";
		try {
			is = PropertiesUtil.class.getResourceAsStream(name);
			properties.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		MP_URL = properties.getProperty("MP_URL");
		MP_APPID = properties.getProperty("MP_APPID");
		MP_PLATFORM_TOKEN = properties.getProperty("MP_PLATFORM_TOKEN");
	}
	
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	public static String getProperty(String key, String def) {
		return properties.getProperty(key, def);
	}

}
