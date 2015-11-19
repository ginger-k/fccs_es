package com.fccs.es_api.vo;

import java.io.Serializable;
import java.util.List;

public class EsPageBean<T> implements Serializable {
	private static final long serialVersionUID = -4389260854845533137L;
	private int pageSize; //每页个数
	private int pageNow; //当前第几页
	private int totalPage; //总页数
	private int totalRecord; //总记录数
	private List<T> items;//当前页的数据 
	
	public EsPageBean() {
		
	}

	public EsPageBean(int pageSize, int pageNow, int totalPage, int totalRecord, List<T> items) {
		super();
		this.pageSize = pageSize;
		this.pageNow = pageNow;
		this.totalPage = totalPage;
		this.totalRecord = totalRecord;
		this.items = items;
	}



	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNow() {
		return pageNow;
	}
	public void setPageNow(int pageNow) {
		this.pageNow = pageNow;
	}
	public List<T> getItems() {
		return items;
	}
	public void setItems(List<T> items) {
		this.items = items;
	}


	public int getTotalPage() {
		return totalPage;
	}


	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}


	public int getTotalRecord() {
		return totalRecord;
	}


	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	
	
}
