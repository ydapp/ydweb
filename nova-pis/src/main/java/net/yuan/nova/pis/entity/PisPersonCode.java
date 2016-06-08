package net.yuan.nova.pis.entity;
/**
 * 用户编码实体类
 * @author sway
 *
 */
public class PisPersonCode {

	/**
	 * 用户编码
	 */
	private String parent;
	/**
	 * 编码类型
	 */
	private String key;	
	/**
	 * 编码数值
	 */
	private String value;
	
	private String parent_1;
	
	
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getParent_1() {
		return parent_1;
	}
	public void setParent_1(String parent_1) {
		this.parent_1 = parent_1;
	}

	public static enum Status{
		//管理员
		A,
		//app管理员
		D,
		//驻场专员
		G,
		//渠道经理
		J,
		//经纪公司
		M,
		//业务员
		P
	}
}
