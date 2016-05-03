package net.yuan.nova.pis.entity.vo;

import java.lang.reflect.InvocationTargetException;

import net.yuan.nova.pis.entity.PisRecommend;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;

public class PisRecommendVo extends PisRecommend {

	// 城市名称
	private String cityName;
	// 楼盘名称
	private String buildingName;
	// 推荐人
	private String refreeName;
	private String refreeNick;
	// 客户到场确认人
	private String customerPresentUserName;
	private String customerPresentUserNick;
	// 推荐确认人
	private String recommendConfirmUserName;
	private String recommendConfirmUserNick;

	public PisRecommendVo(PisRecommend pisRecommend) {
		try {
			ConvertUtils.register(new DateConverter(null), java.util.Date.class);
			BeanUtils.copyProperties(this, pisRecommend);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public String getRefreeName() {
		return refreeName;
	}

	public void setRefreeName(String refreeName) {
		this.refreeName = refreeName;
	}

	public String getRefreeNick() {
		return refreeNick;
	}

	public void setRefreeNick(String refreeNick) {
		this.refreeNick = refreeNick;
	}

	public String getCustomerPresentUserName() {
		return customerPresentUserName;
	}

	public void setCustomerPresentUserName(String customerPresentUserName) {
		this.customerPresentUserName = customerPresentUserName;
	}

	public String getCustomerPresentUserNick() {
		return customerPresentUserNick;
	}

	public void setCustomerPresentUserNick(String customerPresentUserNick) {
		this.customerPresentUserNick = customerPresentUserNick;
	}

	public String getRecommendConfirmUserName() {
		return recommendConfirmUserName;
	}

	public void setRecommendConfirmUserName(String recommendConfirmUserName) {
		this.recommendConfirmUserName = recommendConfirmUserName;
	}

	public String getRecommendConfirmUserNick() {
		return recommendConfirmUserNick;
	}

	public void setRecommendConfirmUserNick(String recommendConfirmUserNick) {
		this.recommendConfirmUserNick = recommendConfirmUserNick;
	}

}
