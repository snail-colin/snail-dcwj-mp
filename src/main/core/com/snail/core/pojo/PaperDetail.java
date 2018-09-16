package com.snail.core.pojo;

import java.io.Serializable;

public class PaperDetail implements Serializable{

	
	public PaperDetail() {
		super();
	}
	
	

	public PaperDetail(String uuid) {
		super();
		this.uuid = uuid;
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 唯一标识
	 */
	public String uuid;
	
	/**
	 * 作答时间
	 */
	public Integer time;
	
	/**
	 * 分数
	 */
	public Integer score;
	
	/**
	 * 报考车型
	 */
	public String  bkcx;
	
	/**
	 * 报考科目
	 */
	public String bkkm;
	
	/**
	 * 考试地点
	 */
	public String ksdd;
	
	/**
	 * 提交时间
	 */
	public String tjsj;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}



	public Integer getTime() {
		return time;
	}



	public void setTime(Integer time) {
		this.time = time;
	}



	public Integer getScore() {
		return score;
	}



	public void setScore(Integer score) {
		this.score = score;
	}



	public String getBkcx() {
		return bkcx;
	}



	public void setBkcx(String bkcx) {
		this.bkcx = bkcx;
	}



	public String getBkkm() {
		return bkkm;
	}



	public void setBkkm(String bkkm) {
		this.bkkm = bkkm;
	}



	public String getKsdd() {
		return ksdd;
	}



	public void setKsdd(String ksdd) {
		this.ksdd = ksdd;
	}



	public String getTjsj() {
		return tjsj;
	}



	public void setTjsj(String tjsj) {
		this.tjsj = tjsj;
	}



	@Override
	public String toString() {
		return "PaperDetail [uuid=" + uuid + ", time=" + time + ", score=" + score + ", bkcx=" + bkcx + ", bkkm=" + bkkm
				+ ", ksdd=" + ksdd + ", tjsj=" + tjsj + "]";
	}




}
