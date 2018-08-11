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
}
