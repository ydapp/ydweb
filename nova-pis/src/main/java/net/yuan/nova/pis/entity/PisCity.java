package net.yuan.nova.pis.entity;
/**
 * 城市
 * @author leasonlive
 *
 */
public class PisCity {
	//城市id
	private String cityId;
	//城市名称
	private String cityName;
	//上级城市id
	private String parentCityId;
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getParentCityId() {
		return parentCityId;
	}
	public void setParentCityId(String parentCityId) {
		this.parentCityId = parentCityId;
	}
	
}
