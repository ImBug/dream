package com.water.util.list;

/**
 * 内存分页
 * @author honghm
 *
 */
public class Page {
	
	private int page_size=10;
	private int current_page=1;//页面从第0页开始
	private int total;
	
	public Page(){super();}
	
	public Page(int total) {
		super();
		this.total = total;
	}
	
	public int getTotal_page(){
		int page = total/page_size;
		int remain = total - page*page_size;
		if(remain > 0){
			return page + 1;
		}
		return page;
	}
	
	public int getPage_size() {
		return page_size;
	}

	public void setPage_size(int page_size) {
		this.page_size = page_size;
	}

	/**
	 * 起始索引
	 * @return
	 */
	public int getStartIndex(){
		return page_size * (current_page-1);
	}
	
	/**
	 * 结束索引
	 * @return
	 */
	public int getEndIndex(){
		int end = page_size * current_page-1;
		return end < (total-1)?end:total-1;
	}
	
	public int getCurrent_page() {
		return current_page;
	}

	public void setCurrent_page(int current_page) {
		this.current_page = current_page;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
}
