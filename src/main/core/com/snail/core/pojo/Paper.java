package com.snail.core.pojo;

import java.io.Serializable;
import java.util.Map;

/**
 * 试卷
 * @author colin
 *
 */
public class Paper implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 试卷唯一标识集合
	 */
	private Map<String, PaperDetail> uuid;

	public Map<String, PaperDetail> getUuid() {
		return uuid;
	}

	public void setUuid(Map<String, PaperDetail> uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return "Paper [uuid=" + uuid + "]";
	}

}
