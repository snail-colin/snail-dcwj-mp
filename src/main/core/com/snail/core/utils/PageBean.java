package com.snail.core.utils;

import java.util.List;

public class PageBean {

	
	/**
	 * 当前页数
	 */
	private Integer currentPage;

	/**
	 * 每页条数
	 */
	private Integer pageSize;

	private Integer recordCount;

	/**
	 * total 总数
	 */
	private Integer total;

	/**
	 * 总页数
	 */
	private Integer pageCount;
	
	/**
	 * 类型
	 */
	private Integer type;

	/**
	 * 结果集
	 */
	private List<?> resultList;

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(Integer recordCount) {
		this.recordCount = recordCount;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	public List<?> getResultList() {
		return resultList;
	}

	public void setResultList(List<?> resultList) {
		this.resultList = resultList;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "PageBean [currentPage=" + currentPage + ", pageSize=" + pageSize + ", recordCount=" + recordCount
				+ ", total=" + total + ", pageCount=" + pageCount + ", type=" + type + ", resultList=" + resultList
				+ "]";
	}
	
}