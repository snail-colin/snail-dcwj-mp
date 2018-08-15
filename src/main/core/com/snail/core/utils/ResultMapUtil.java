package com.snail.core.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回结果工具
 *
 */
public class ResultMapUtil {
	
	public static Map<String, Object> toMap(int code, String msg) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", code);
		map.put("msg", msg);
		return map;
	}
	

	public static Map<String, Object> toMap(Map<String, Object> map ,int code, String msg) {
		map.put("code", code);
		map.put("msg", msg);
		return map;
	}
}
