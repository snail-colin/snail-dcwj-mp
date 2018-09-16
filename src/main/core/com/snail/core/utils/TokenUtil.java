package com.snail.core.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snail.core.pojo.Token;

public class TokenUtil {
	
	private static final Logger log = LoggerFactory.getLogger(TokenUtil.class);
	
	/**
	 * 获取MP 的 access_token
	 * @return access_token
	 */
	public static synchronized String getToken() {
		String field = "mp_access_token";
		Token token = CacheUtil.getCache(Token.class, field);
		if (token == null || hasTimeOut(token)) { //没有缓存或者缓存失效
			token = getTokenFromMP();
			CacheUtil.updateCache(field, token);
		}
		return token.token;
	}
	
	
	/**
	 * 获取MP 的 access_token
	 * @return access_token
	 */
	public static synchronized String getToken(String bn) {
		String field = "mp_access_token";
		Token token = CacheUtil.getCache(Token.class, field);
		if (token == null || hasTimeOut(token)) { //没有缓存或者缓存失效
			token = getTokenFromMP(bn);
			CacheUtil.updateCache(field, token);
		}
		return token.token;
	}
	
	/**
	 * 判断缓存是否超时
	 * @param token
	 * @return
	 */
	private static boolean hasTimeOut(Token token) {
		long activeTime = System.currentTimeMillis() - token.getTime;
		activeTime = activeTime/1000; //毫秒转换成秒
		if (activeTime > token.expiresIn/2 - 600) { //在有效期前10分钟就需要重新下载
			return true;
		}
		return false;
	}
	

	@SuppressWarnings("unchecked")
	public static Token getTokenFromMP() {
		Token token =null;
		String url = PropertiesUtil.MP_URL + "/api/token";
		log.info("[TokenUtil][getTokenFromMP][url={}]",url);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appid",PropertiesUtil.MP_APPID);
		params.put("platformToken", PropertiesUtil.MP_PLATFORM_TOKEN);
		log.info("[TokenUtil][getTokenFromMP][params={}]",params);
		String result = HttpClientUtil.getRequst(params, url);
		log.info("[TokenUtil][getTokenFromMP][result={}]",result);
		if(StringUtils.isNotBlank(result)) {
			Map<String, Object> ret;
			try {
				ret = ConvertorUtil.objectMapper.readValue(result, Map.class);
				if((Integer)ret.get("status") == 1) {
					ret = (LinkedHashMap<String, Object>) ret.get("data");
					token	= new Token();
					token.getTime = System.currentTimeMillis();
					token.token = (String) ret.get("access_token");
					token.expiresIn = (Integer) ret.get("expires_in");
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("[TokenUtil][getTokenFromMP][e={}]",e);
			}
		}
		log.info("[TokenUtil][getTokenFromMP][token={}]",token);
		return token;
	}
	
	@SuppressWarnings("unchecked")
	public static Token getTokenFromMP(String bn) {
		Token token =null;
		String url = PropertiesUtil.MP_URL + "/api/token";
		log.info("[TokenUtil][getTokenFromMP][url={}]",url);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appid", PropertiesUtil.getProperty("MP_APPID_"+bn));
		params.put("platformToken", PropertiesUtil.getProperty("MP_PLATFORM_TOKEN_"+bn) );
		log.info("[TokenUtil][getTokenFromMP][params={}]",params);
		String result = HttpClientUtil.getRequst(params, url);
		log.info("[TokenUtil][getTokenFromMP][result={}]",result);
		if(StringUtils.isNotBlank(result)) {
			Map<String, Object> ret;
			try {
				ret = ConvertorUtil.objectMapper.readValue(result, Map.class);
				if((Integer)ret.get("status") == 1) {
					ret = (LinkedHashMap<String, Object>) ret.get("data");
					token	= new Token();
					token.getTime = System.currentTimeMillis();
					token.token = (String) ret.get("access_token");
					token.expiresIn = (Integer) ret.get("expires_in");
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("[TokenUtil][getTokenFromMP][e={}]",e);
			}
		}
		log.info("[TokenUtil][getTokenFromMP][token={}]",token);
		return token;
	}
	
	public static void main(String[] args) {
		 Token token  =getTokenFromMP();
		 System.out.println(token );
	}
}
