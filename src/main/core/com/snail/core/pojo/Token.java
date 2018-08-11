package com.snail.core.pojo;

public class Token {

	/**
	 * 获取时间
	 */
	public Long getTime;
	/**
	 * token值
	 */
	public String token;
	/**
	 * 有效时长（秒）
	 */
	public Integer expiresIn;
	
	@Override
	public String toString() {
		return "Token [getTime=" + getTime + ", token=" + token + ", expiresIn=" + expiresIn + "]";
	}
	
}
