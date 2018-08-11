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
	private Long total;

	/**
	 * 总页数
	 */
	private Integer pageCount;

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

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "PageBean [currentPage=" + currentPage + ", pageSize=" + pageSize + ", recordCount=" + recordCount
				+ ", total=" + total + ", pageCount=" + pageCount + ", resultList=" + resultList + "]";
	}

}