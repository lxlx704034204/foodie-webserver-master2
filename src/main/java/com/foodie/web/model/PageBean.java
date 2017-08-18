package com.foodie.web.model;

public class PageBean {
	private int pageSize;// 
	private int recordCount;// 
	private int currentPage;// 
	private int pageCount;// 

	public PageBean(int pageSize, int recordCount, int currentPage) {
		this.pageSize = pageSize;
		this.recordCount = recordCount;
		this.setCurrentPage(currentPage);
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	/**
	 *
	 */
	public int getPageCount() {
		pageCount = recordCount / pageSize;
		int mod = recordCount % pageSize;
		if (mod != 0) {
			pageCount++;
		}
		return pageCount == 0 ? 1 : pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 *
	 */
	private void setCurrentPage(int currentPage) {
		int activePage = currentPage <= 0 ? 1 : currentPage;
		activePage = activePage > getPageCount() ? getPageCount() : activePage;
		this.currentPage = activePage;
	}

	public int getFromIndex() {
		return (currentPage - 1) * pageSize;
	}

	public int getToIndex() {
		return Math.min(recordCount, currentPage * pageSize);
	}

}