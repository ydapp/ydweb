package net.yuan.nova.pis.pagination;
/**
 * 分页参数
 * @author leasonlive
 *
 */
public class PageParam {
	//第几页
	private int page;
	//每页显示多少条
	private int pageSize;
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
}
