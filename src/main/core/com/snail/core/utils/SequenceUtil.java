package com.snail.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SequenceUtil {
	
	private static Map<String, Integer> uidSequence = new ConcurrentHashMap<String, Integer>();
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static String getDid(int len) {
		String time = new SimpleDateFormat("yyyyMMdd").format(new Date());
		return getId(time, len);
	}
	
	/**
	 * @param len 序列号长度
	 * @return 用户ID memberNo yyyyMMddHHmmss+x...x(len位序列号)
	 */
	public static String getUid(int len) {
		String time = sdf.format(new Date());
		return getId(time, len);
	}
	
	public static  synchronized String getId(String pre, int len) {
		int sequence = 1;
		if (uidSequence.containsKey(pre)) {
			sequence = uidSequence.get(pre) + 1;
		}
		uidSequence.put(pre, sequence);
		pre += StringUtil.adjustString(sequence + "", '0', len, true);
		return pre;
	}

}
