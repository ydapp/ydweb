package net.yuan.nova.pis.entity;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 楼盘
 * @author leasonlive
 *
 */
public class PisProperty {
    //楼盘id
    private String propertyId;

    //楼盘名称
    private String propertyName;

    //楼盘类型
    private String propertyType;

    //房地产公司
    private String realEstateAgency;

    /*楼盘地址*/
    private String address;

    //国家名称
    private String country;
    //省份
    private String province;
    //城市
    private String city;
    //城市名称
    private String cityTitle;
    //国家
    private String county;
    //开盘日期
    private Date openDate;
    //特色
    private String characteristic;
    //装修
    private String decoration;

    //面积
    private String area;

    //产权年限
    private Integer years;

    //绿化率
    private BigDecimal greenRate;

    //物业公司
    private String propertyCompany;

    //交付时间
    private Date deliveryTime;

    //均价
    private Integer avgPrice;

    //佣金
    private String commission;

    //推荐次数
    private Integer recommendedNumber;

    //预约次数，也是认购次数（在推荐中回填-订操作)
    private Integer reservationNumber;

    //查看次数,成交次数(在推荐中回填-购买操作)
    private Integer viewTimes;
    //认购规则
    private String subscriptionRules;
    //楼盘电话
    private String propertyTel;
    //交通配套
    private String trafficFacilities;
    //户型介绍
    private String houseType;
    /**
     * 楼盘id
     * @return
     */
    public String getPropertyId() {
        return propertyId;
    }

    /**
     * 楼盘id
     */
    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId == null ? null : propertyId.trim();
    }
    /**
     * 楼盘名称
     * @return
     */
    public String getPropertyName() {
        return propertyName;
    }
    /**
     * 楼盘名称
     * @param propertyName
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName == null ? null : propertyName.trim();
    }
    /**
     * 楼盘类型
     * @return
     */
    public String getPropertyType() {
        return propertyType;
    }
    /**
     * 楼盘类型
     * @param propertyType
     */
    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType == null ? null : propertyType.trim();
    }
    /**
     * 房地产公司
     * @return
     */
    public String getRealEstateAgency() {
        return realEstateAgency;
    }
    /**
     * 房地产公司
     * @param realEstateAgency
     */
    public void setRealEstateAgency(String realEstateAgency) {
        this.realEstateAgency = realEstateAgency;
    }
    /**
     * 地址
     * @return
     */
    public String getAddress() {
        return address;
    }
    /**
     * 地址
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }
    /**
     * 国家
     */
    public String getCountry() {
        return country;
    }
    /**
     * 国家
     * @param country
     */
    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }
    /**
     * 省份
     * @return
     */
    public String getProvince() {
        return province;
    }

    /**
     * 省份
     * @param province 
     */
    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    /**
     * 城市
     * @return 
     */
    public String getCity() {
        return city;
    }

    /**
     * 城市
     */
    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    /**
     * 国家
     */
    public String getCounty() {
        return county;
    }

    /**
     * 国家
     */
    public void setCounty(String county) {
        this.county = county == null ? null : county.trim();
    }

    /**
     * 开盘日期
     */
    public Date getOpenDate() {
        return openDate;
    }

    /**
     * 开盘日期
     */
    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    /**
     * 特色
     */
    public String getCharacteristic() {
        return characteristic;
    }

    /**
     * 特色
     */
    public void setCharacteristic(String characteristic) {
        this.characteristic = characteristic == null ? null : characteristic.trim();
    }

    /**
     * 装修
     */
    public String getDecoration() {
        return decoration;
    }

    /**
     * 装修
     */
    public void setDecoration(String decoration) {
        this.decoration = decoration == null ? null : decoration.trim();
    }

    /**
     * 面积
     */
    public String getArea() {
        return area;
    }

    /**
     * 面积
     */
    public void setArea(String area) {
        this.area = area == null ? null : area.trim();
    }

    /**
     * 产权年限
     */
    public Integer getYears() {
        return years;
    }

    /**
     * 产权年限
     */
    public void setYears(Integer years) {
        this.years = years;
    }

    /**
     * 绿化率
     */
    public BigDecimal getGreenRate() {
        return greenRate;
    }

    /**
     * 绿化率
     */
    public void setGreenRate(BigDecimal greenRate) {
        this.greenRate = greenRate;
    }

    /**
     * 物业公司
     */
    public String getPropertyCompany() {
        return propertyCompany;
    }

    /**
     * 物业公司
     */
    public void setPropertyCompany(String propertyCompany) {
        this.propertyCompany = propertyCompany == null ? null : propertyCompany.trim();
    }

    /**
     * 交付时间
     */
    public Date getDeliveryTime() {
        return deliveryTime;
    }

    /**
     * 交付时间
     */
    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    /**
     * 均价
     */
    public Integer getAvgPrice() {
        return avgPrice;
    }

    /**
     * 均价
     */
    public void setAvgPrice(Integer avgPrice) {
        this.avgPrice = avgPrice;
    }

    /**
     * 佣金
     */
    public String getCommission() {
        return commission;
    }

    /**
     * 佣金
     */
    public void setCommission(String commission) {
        this.commission = commission == null ? null : commission.trim();
    }

    /**
     * 推荐次数
     */
    public Integer getRecommendedNumber() {
        return recommendedNumber;
    }

    /**
     * 推荐次数
     */
    public void setRecommendedNumber(Integer recommendedNumber) {
        this.recommendedNumber = recommendedNumber;
    }

    /**
     * 预约数
     */
    public Integer getReservationNumber() {
        return reservationNumber;
    }

    /**
     * 预约数
     */
    public void setReservationNumber(Integer reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    /**
     * 查看次数
     */
    public Integer getViewTimes() {
        return viewTimes;
    }

    /**
     * 查看次数
     */
    public void setViewTimes(Integer viewTimes) {
        this.viewTimes = viewTimes;
    }
    /**
     * 城市名称，需要预先额外设置
     * @return
     */
	public String getCityTitle() {
		return cityTitle;
	}
	/**
	 * 城市名称
	 * @param cityTitle
	 */
	public void setCityTitle(String cityTitle) {
		this.cityTitle = cityTitle;
	}
	/**
	 * 认购规则
	 * @return
	 */
	public String getSubscriptionRules() {
		return subscriptionRules;
	}
	/**
	 * 认购规则
	 * @param subscriptionRules
	 */
	public void setSubscriptionRules(String subscriptionRules) {
		this.subscriptionRules = subscriptionRules;
	}

	/**
	 * 楼盘电话
	 * @return 
	 */
	public String getPropertyTel() {
		return propertyTel;
	}

	/**
	 * 楼盘电话
	 * @param 楼盘电话
	 */
	public void setPropertyTel(String propertyTel) {
		this.propertyTel = propertyTel;
	}
	/**
	 * 交通配套
	 * @return
	 */
	public String getTrafficFacilities() {
		return trafficFacilities;
	}
	/**
	 * 交通配套
	 * @param trafficFacilities
	 */
	public void setTrafficFacilities(String trafficFacilities) {
		this.trafficFacilities = trafficFacilities;
	}
	/**
	 * 户型介绍
	 * @return
	 */
	public String getHouseType() {
		return houseType;
	}
	/**
	 * 户型介绍
	 * @param houseType
	 */
	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}
    
}