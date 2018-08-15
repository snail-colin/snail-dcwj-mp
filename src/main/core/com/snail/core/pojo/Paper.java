package com.snail.core.pojo;

import java.io.Serializable;
import java.util.Map;

public class Paper implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 试卷唯一标识集合
	 */
	private Map<String, Object> uuid;


	public Map<String, Object> getUuid() {
		return uuid;
	}


	public void setUuid(Map<String, Object> uuid) {
		this.uuid = uuid;
	}


	@Override
	public String toString() {
		return "Paper [uuid=" + uuid + "]";
	}
	
}
