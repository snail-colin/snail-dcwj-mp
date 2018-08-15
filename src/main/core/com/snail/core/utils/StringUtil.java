package com.snail.core.utils;

public class StringUtil {
	public static boolean isEmpty(String str){
		if(str == null || "".equals(str)){
			return true;
		}else{
			return false;
		}
	}
	public static boolean isNotEmpty(String str){
		if(str != null && !"".equals(str)){
			return true;
		}else{
			return false;
		}
	}
	public static boolean isNotEmptyEx(String value) {
        return ! isEmptyEx(value);
    }
	public static boolean isEmptyEx(String value) {
		return value == null || value.length() == 0 
    			|| "null".equalsIgnoreCase(value) 
    			|| "N/A".equalsIgnoreCase(value);
	}
	
	/**
	 * <p>
	 * 在一个字符串的前面或后面增加一些填充字符。
	 * @param value 转换前的的字符串
	 * @param fill 需要填充的字符
	 * @param length 转换后字符串的长度,如果此值比value.length小的话，则返回 value的前length个字符
	 * @param head true:在value前填充字符；false:在value后填充字符
	 * @return 返回转换后的字符串。如果length为0或value为null的话，则返回null
	 */
	public static String adjustString(String value, char fill, int length,
			boolean head) {
		if (value == null || length < 0)
			return null;
		if (value.length() >= length)
			return value.substring(0, length);
		else {
			StringBuffer sb = new StringBuffer().append(fill);
			int len = length - value.length();
			while (len != 1) {
				len /= 2;
				sb.append(sb);
			}
			sb.append(sb).setLength(length - value.length());
			if (head)
				return sb.append(value).toString();
			else
				return sb.insert(0, value).toString();
		}
	}
}
