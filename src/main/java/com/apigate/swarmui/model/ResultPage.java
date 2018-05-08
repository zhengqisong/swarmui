package com.apigate.swarmui.model;

import java.util.List;

import com.github.pagehelper.Page;

public class ResultPage<T> {
	int pages;
	int pageNum;
	int pageSize;
	long totalRecode;
	List<T> list;
	public ResultPage(){		
	}
	
	public ResultPage(Page<T> page){
		if(page != null){
			this.pageNum = page.getPageNum();
			this.pages = page.getPages();
			this.pageSize = page.getPageSize();
			this.totalRecode = page.getTotal();
			this.list = page.getResult();
			
		}
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotalRecode() {
		return totalRecode;
	}

	public void setTotalRecode(long totalRecode) {
		this.totalRecode = totalRecode;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	
}
