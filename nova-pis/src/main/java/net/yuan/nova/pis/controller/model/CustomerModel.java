package net.yuan.nova.pis.controller.model;
/**
 * 客户模型
 * @author leasonlive
 *
 */

import java.util.ArrayList;
import java.util.List;
public class CustomerModel{
	//客户名称
	private String customerName;
	//客户电话
	private String customerTel;
	//推荐次数
	private int refreeCount;
	//推荐信息
	private List<CustomerRefreeInfo> refreeList = new ArrayList<CustomerRefreeInfo>();
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerTel() {
		return customerTel;
	}
	public void setCustomerTel(String customerTel) {
		this.customerTel = customerTel;
	}
	public int getRefreeCount() {
		return refreeCount;
	}
	public void setRefreeCount(int refreeCount) {
		this.refreeCount = refreeCount;
	}
	public List<CustomerRefreeInfo> getRefreeList() {
		return refreeList;
	}
	public void setRefreeList(List<CustomerRefreeInfo> refreeList) {
		this.refreeList = refreeList;
	}
	
}
